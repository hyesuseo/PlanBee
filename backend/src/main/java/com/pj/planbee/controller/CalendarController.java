package com.pj.planbee.controller;

import com.pj.planbee.dto.CalendarDTO;
import com.pj.planbee.dto.ProgressDTO;
import com.pj.planbee.service.CalendarService;
import com.pj.planbee.service.TodoListService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired CalendarService cs;
  
    //일별 진척도
     @GetMapping(value="/dprogress/{calDate}", produces = "application/json;charset=UTF-8") //날짜를 가져와서 일별 진척도를 확인
     public double getProgress(@PathVariable String calDate, HttpSession session) {
         String sessionId = (String) session.getAttribute("sessionId");
      // getProgress 메소드를 호출하여 해당 날짜의 진척도 계산 후 반환
         return cs.getProgress(calDate, sessionId);
     }
     //월별 진척도
     @GetMapping(value="/mprogress/{yyMM}", produces = "application/json;charset=UTF-8")
     public double monthProgress(@PathVariable String yyMM,HttpSession session) {
    	 // 년, 월만 받아와서 해당 월의 데이터를 가져옴
    	 
    	 String sessionId = (String) session.getAttribute("sessionId");
    	 return cs.monthProgress(yyMM, sessionId);
     }
     
     @GetMapping(value = "/curStreak", produces = "application/json;charset=UTF-8")
     public ResponseEntity<Map<String, Object>> curStreak(HttpSession se) {
         String sessionId = (String) se.getAttribute("sessionId");

         // 현재 및 최대 연속 달성일 정보 가져오기
         Map<String, Integer> result = cs.curProgress(sessionId);
         int curStreak = result.get("curStreak");
         int maxStreak = result.get("maxStreak");
         
         // 응답 데이터 구성
         Map<String, Object> response = new HashMap<>();
         response.put("curStreak", curStreak);
         response.put("maxStreak", maxStreak);

         // 연속 달성일 상태 메시지 결정
         String message;
         if (curStreak == maxStreak && curStreak > 0) {
             message = "신기록 갱신중";
         } else if (curStreak >= maxStreak * 0.5) {
             message = "거의 다 왔어요 !";
         } else {
             message = "조금만 더 힘내요 !";
         }

         response.put("message", message);

         return ResponseEntity.ok(response);
     }
     //최대 연속 달성일
     @GetMapping(value="/maxStreak", produces="application/json;charset=UTF-8")
     public int maxStreak(HttpSession se) {
         String sessionId = (String) se.getAttribute("sessionId");
         Map<String, Integer> result = cs.curProgress(sessionId); // 결과 값을 받아오기
 
         return result.get("maxStreak");
     }

    //메모 조회
     @GetMapping(value = "/memo/{yyMM}", produces="application/json;charset=UTF-8")
     public Map<String, ProgressDTO> getMemo(@PathVariable String yyMM, HttpSession session) {
         String sessionId = (String) session.getAttribute("sessionId");
         // 1) 한 달치 메모 + 진행률 조회
         List<ProgressDTO> memoList = cs.getMemo(yyMM, sessionId);

         // 2) calDate 예: "250301" → 뒤 2자리 "01"를 key로 해서 Map 생성
         //    만약 같은 일자에 여러 건이 있을 수 있다면, groupingBy를 사용해 Map<String, List<CalendarDTO>>로 만들 수도 있음.
         return memoList.stream()
             .collect(Collectors.toMap(
                 dto -> dto.getCalDate().substring(4, 6),  // "250301" -> "01"
                 dto -> dto
             ));
     }

    //메모추가
    @PostMapping(value="/addmemo/{calDate}", produces="application/json;charset=UTF-8")
    // CalendarDTO 객체에 날짜(calDate)와 사용자 아이디를 설정한 후, DB에 메모를 저장
    public int addMemo(@PathVariable String calDate, @RequestBody CalendarDTO calendar, HttpSession se) {
        String sessionId = (String) se.getAttribute("sessionId");
        calendar.setCalDate(calDate);
        calendar.setUserId(sessionId);
        return cs.addMemo(calendar);
    }

    //메모 수정
    @PutMapping("/modimemo/{calId}")
    public int modiMemo(@PathVariable int calId, @RequestBody CalendarDTO calendar) {
    	 // 수정할 메모의 식별자를 CalendarDTO에 설정
    	System.out.println("memoPut: 요청들어옴");
    	 calendar.setCalId(calId);
    	System.out.println("calID:" + calId);
    	int result = cs.modiMemo(calendar);
    	System.out.println("결과값: "+ result);
    	return result;
    }
    
    //메모 삭제 성공 여부는 1, 0으로
    @DeleteMapping("/delmemo/{calId}/{fieldNo}")
    public int delMemo(@PathVariable int calId, @PathVariable int fieldNo) {
        return cs.delMemo(calId, fieldNo);
    }
}
    