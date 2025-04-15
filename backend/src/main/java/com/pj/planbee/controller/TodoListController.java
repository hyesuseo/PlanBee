package com.pj.planbee.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pj.planbee.config.CacheConfig;
import com.pj.planbee.dto.TDdetailDTO;
import com.pj.planbee.dto.TodoDashboardDTO;
import com.pj.planbee.dto.TodoListDTO;
import com.pj.planbee.dto.TodoListDTO.SubTodoListDTO;
import com.pj.planbee.service.TodoListService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@RestController
@RequestMapping("/todolist")
@CrossOrigin(origins="http://localhost:3000", allowCredentials ="true")
public class TodoListController {

	private final TodoListService ts;
	private final CacheConfig cacheConfig;


	public TodoListController(TodoListService ts, CacheConfig cacheConfig) {
		this.cacheConfig = cacheConfig;
		this.ts = ts;
	}

    @GetMapping(value = "/dashBoard/{tdDate}", produces = "application/json; charset=utf-8")
    public TodoDashboardDTO dashboardData(@PathVariable String tdDate, HttpSession se) {
    	String sessionId = (String) se.getAttribute("sessionId");
    	System.out.println("todolist sessionId" + sessionId);
    	TodoDashboardDTO dashboard = new TodoDashboardDTO();
    	int todoId;
    	//1. 캐시에서 먼저 검색한다
    	Integer savedId = cacheConfig.getTodoIdCache(sessionId, tdDate);
    	
    	//2. 캐시값이 null이 아니면, 그 캐시에 해당하는 todoId를 불러옴
    	if ( savedId != null ) { 
    		todoId = savedId;
    		System.out.println("캐시에 존재하는 값"+ todoId);
    	} else {
    		//3. 캐시값이 null이면 checkRow를 수행한다.
    		int result = ts.checkRow(tdDate, sessionId);
        	System.out.println("해당날짜에 해당하는 열번호"+ result);
        	if(result == 0) { //열이 없으면 한 열을 입력하고 그 결과를 반환한다.
        		ts.inputRow(tdDate, sessionId);
        		todoId = ts.tdIdSearch(tdDate, sessionId);
        	}else { //열이 있으면 그 결과값을 반환한다.
        		todoId = result;
        	}
        	//null인 경우 열 검색다 하고 캐시에 저장도 한다.
        	cacheConfig.putTodoIdCache(sessionId, tdDate, todoId);
        	System.out.println("캐시에 저장 완료" + todoId);
    	}
    	
    	
    	dashboard.setTodoId(todoId);
    	
    	//todoDetail들을 리스트 형식으로 가져온다.
    	List<TDdetailDTO> list = ts.getTodo(todoId); //한 줄로 쓰는게 더 개선된 부분이라고 함
    	dashboard.setTodoList(list);
    	System.out.println("그날짜의 todolist"+list);
    	
    	//todoMemo를 값으로 가져온다.
    	String memo = ts.getMemo(todoId);
    	dashboard.setMemo(memo);
    	
    	//진척도를 값으로 가져온다.
    	double progress = ts.todoProgress(todoId);
        ts.regiProgress(todoId, progress);
        dashboard.setProgress(progress);
        
    return dashboard;
    }
    @GetMapping(value = "/getTodo/{tdDate}", produces = "application/json; charset=utf-8") 
    public List<TDdetailDTO> getToday(//getTodo삭제
            @ApiParam(value = "YYMMDD 형식의 날짜 (예: 230315)", required = true) 
            @PathVariable String tdDate,
            HttpSession se) {
        String sessionId = (String) se.getAttribute("sessionId");
        System.out.println("todolist sessionId" + sessionId);
        int todoId;
        int result = ts.checkRow(tdDate, sessionId);
        
        if (result == 0) {
            ts.inputRow(tdDate, sessionId);
            todoId = ts.tdIdSearch(tdDate, sessionId);
        } else {
            todoId = result;
        }
        System.out.println("debug: "+ todoId);
        List<TDdetailDTO> list = new ArrayList<>();
        list = ts.getTodo(todoId);
        return list;
    }

    @ApiOperation(value = "투두리스트 작성", 
                  notes = "YYMMDD 형식의 날짜와 함께 투두리스트의 세부내용을 작성합니다. 작성 성공 시 입력된 tdDetailId를 반환합니다.")
    @PostMapping(value = "/write/{tdDate}", produces = "application/json; charset=utf-8")
    @ResponseBody
    public Map<String, Integer> todoWrite(
            @ApiParam(value = "투두리스트 작성 정보 (TDdetailDTO)", required = true) 
            @RequestBody TDdetailDTO dto,
            @ApiParam(value = "YYMMDD 형식의 날짜 (예: 230315)", required = true) 
            @PathVariable String tdDate,
            HttpSession se) {
        String sessionId = (String) se.getAttribute("sessionId");
        int tdId = ts.tdIdSearch(tdDate, sessionId);
        dto.setTdId(tdId);
        
        Map<String, Integer> response = new HashMap<>();
        int result = ts.todoWrite(dto);
        int returnTdDetailId = ts.getTdDetailId(dto.getTdDetail(), tdId);
        
        if (result == 1) {
            response.put("tdDetailId", returnTdDetailId);
            double newProgress = ts.todoProgress(tdId);
            ts.regiProgress(tdId, newProgress);
            return response;
        } else {
            return null;
        }
    }

    @ApiOperation(value = "투두리스트 삭제", 
                  notes = "TDdetailDTO 내의 tdDetailId를 이용하여 해당 투두리스트 항목을 삭제합니다.")
    @DeleteMapping(value = "/del", produces = "application/json; charset=utf-8")
    public int todoDel(
            @ApiParam(value = "삭제할 투두리스트의 tdDetailId를 포함한 TDdetailDTO", required = true) 
            @RequestBody TDdetailDTO dto) {
        return ts.todoDel(dto.getTdDetailId());
    }

    @ApiOperation(value = "투두리스트 완료 상태 업데이트", 
                  notes = "TDdetailDTO 내의 tdDetailId와 tdDetailState를 이용해 투두리스트 항목의 완료 상태를 업데이트하고, 최신 진척도를 반환합니다.")
    @PutMapping(value = "/state", produces = "application/json; charset=utf-8")
    public double updateState(
            @ApiParam(value = "업데이트할 투두리스트 정보 (TDdetailDTO: tdDetailId, tdDetailState, tdId)", required = true) 
            @RequestBody TDdetailDTO dto,
            HttpSession se) {
        ts.updateState(dto.getTdDetailId(), dto.isTdDetailState());
        double progress = ts.todoProgress(dto.getTdId());
        ts.regiProgress(dto.getTdId(), progress);
        return progress;
    }

    @ApiOperation(value = "투두리스트 수정", 
                  notes = "TDdetailDTO를 이용해 투두리스트 항목의 내용을 수정합니다.")
    @PutMapping(value = "/modify", produces = "application/json; charset=utf-8")
    public int todoModify(
            @ApiParam(value = "수정할 투두리스트 정보 (TDdetailDTO)", required = true) 
            @RequestBody TDdetailDTO dto) {
        return ts.todoModify(dto);
    }


    @ApiOperation(value = "메모 작성/수정", 
                  notes = "TodoListDTO를 이용해 메모를 작성하거나 수정합니다.")
    @PutMapping(value = "/memoWrite", produces = "application/json; charset=utf-8")
    public int memoWrite(
            @ApiParam(value = "메모 작성 또는 수정 정보 (TodoListDTO)", required = true) 
            @RequestBody TodoListDTO listDto) {
        return ts.memoWrite(listDto);
    }

    @ApiOperation(value = "투두리스트 진척도 조회", 
                  notes = "YYMMDD 형식의 날짜를 입력받아 해당 날짜의 투두리스트 진척도를 계산하여 반환합니다.")
    @GetMapping(value = "/progress/{tdDate}", produces = "application/json; charset=utf-8")
    public double getProgress( //getProgress도 삭제
            @ApiParam(value = "YYMMDD 형식의 날짜 (예: 230315)", required = true) 
            @PathVariable String tdDate,
            HttpSession se) {
        String sessionId = (String) se.getAttribute("sessionId");
        int tdId = ts.tdIdSearch(tdDate, sessionId);
        double progress = ts.todoProgress(tdId);
        ts.regiProgress(tdId, progress);
        return ts.todoProgress(tdId);
    }

    @ApiOperation(value = "테스트용 저장 기능", 
                  notes = "테스트를 위한 저장 기능입니다. (현재 주석 처리되어 있음)")
    @GetMapping(value = "/testSaveDetail", produces = "application/json; charset=utf-8")
    public void testSave(HttpSession se) {
        // ts.saveArchiveDetail();
    }
}
