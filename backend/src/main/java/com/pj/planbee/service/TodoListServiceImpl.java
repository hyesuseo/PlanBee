package com.pj.planbee.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.planbee.dto.ArchDetailDTO;
import com.pj.planbee.dto.ArchiveDTO;
import com.pj.planbee.dto.TDdetailDTO;
import com.pj.planbee.dto.TDstartDTO;
import com.pj.planbee.dto.TodoListDTO;
import com.pj.planbee.dto.TodoListDTO.SubTodoListDTO;
import com.pj.planbee.mapper.SaveArchiveMapper;
import com.pj.planbee.mapper.TDdetailMapper;
import com.pj.planbee.mapper.TodoListMapper;


@Service
public class TodoListServiceImpl implements TodoListService {
@Autowired TDdetailMapper tdMap;
@Autowired TodoListMapper tlMap;
@Autowired SaveArchiveMapper saMap;




public HashMap<String, String> checkToday() { //오늘과 내일 날짜값을 String으로 변환하는 메소드
	LocalDateTime today = LocalDateTime.now();
	LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
	LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
	DateTimeFormatter form = DateTimeFormatter.ofPattern("yyMMdd"); //날짜 변환
	String todayStr = today.format(form); //오늘 날짜를 위 형식으로 변환
	String tomorrowStr = tomorrow.format(form); //내일 날짜를 위 형식으로 변환
	String yesterdayStr = yesterday.format(form); //어제 날짜도 위 형식으로 변환
	//System.out.println("내일날짜 변환: " + tomorrowStr);
	
	HashMap<String, String> todayTomo = new HashMap<String, String>();
	todayTomo.put("todayStr", todayStr);
	todayTomo.put("tomorrowStr", tomorrowStr); //오늘날짜에 대한 것만 입력하는 것이 안정성이 좋을 것 같음
	todayTomo.put("yesterdayStr", yesterdayStr);
	return todayTomo;
}
public int checkRow(String tdDate, String sessionId) { //열이 있는지 확인하는 메소드
	//순환문을 돌리면서 값이 있는지 확인한다.
	List<TDstartDTO> dateId = new ArrayList<TDstartDTO>();
	dateId = tlMap.getDate(sessionId); //sessionId에 해당하는 todoDate와 todoId를 가져온다
	int selectedtdId = 0; //return할 selectedId를 초기화
	//System.out.println("service" + dateId.get(8).getTodo_date());
	
	for(int i =0; i<dateId.size(); i++) { //일치하는 열이 있는지 찾는다.
		if(dateId.get(i).getTodo_date().equals(tdDate)) {
			selectedtdId = dateId.get(i).getTodo_Id();
			break;
		}else {
			selectedtdId = 0;
		}
	}	
	
	return selectedtdId;
}
//@Override
//public void inputRow(String tdDate, String sessionId) { //
//	tlMap.dateWrite(tdDate, sessionId); //열을 작성함	
//	
//}



public int tdIdSearch(String tdDate, String sessionId) { //날짜와 아이디에 해당하는 tdId를 써치하는 메소드
	List<TDstartDTO> dateId = tlMap.getDate(sessionId);
	//System.out.println("service: "+dateId.get(3).getTodo_Id());
	int selectedtdId = 0;
	for (int i =0; i<dateId.size(); i++) {//dateId 리스트를 순회하며,todayStr과 같은 날짜가 있는지 확인 
		if(dateId.get(i).getTodo_date().equals(tdDate)) {
			//리스트 중에 입력한 날짜와 같은 열, 세션아이디와 같은 값을 가진 열을 찾으면 그 고유번호를 반환함
			selectedtdId = dateId.get(i).getTodo_Id(); //for문 사용하여 index번화 반환하므로 1 더해줌
		}
	}
	return selectedtdId;
}

@Override
public List<TDdetailDTO> getTodo(int tdId) { //하루의 투두리스트를 가져오는 기능, 위에서 반환한 todolist고유 아이디로 가져옴
	List<TDdetailDTO> list = new ArrayList<TDdetailDTO>();
	list = tdMap.getTodo(tdId); 
	return list;
}

@Override //투두리스트 작성기능에 if 문 사용해서 todolist 값이 없으면 입력하는 기능을 만들어야함! 
public int todoWrite(TDdetailDTO dto) { //투두리스트 작성하는 기능, 성공시 결과값은 1
	
		int result =0;
		//System.out.println("service impl todoWrite : "+ dto.getTdId());
		try {
			result = tdMap.todoWrite(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result ==1) {
			tlMap.getLatest();
		}
	
	
	return result;
}

@Override
public int updateState(int tdDetailId, boolean state) {  //투두리스트 작업상태 업데이트 하는 기능
	//완료시 True혹은 t, 기본값은 False혹은 f
	int result =0;
	try {
		result = tdMap.updateState(tdDetailId, state);
		System.out.println(result);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}

@Override
public int todoModify(TDdetailDTO dto) { //투두리스트 자체 수정기능
	int result = 0;
	try {
		result = tdMap.todoModify(dto);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}

@Override
public int todoDel(int tdDetailId) { //투두리스트 한 개 삭제하는 기능
	int result = 0;
	try {
		result = tdMap.todoDel(tdDetailId);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}

@Override
public double todoProgress(int tdId) {
	
	double progress = 0.0;
	if(getTodo(tdId).size()==0){
	//todoId로 가져온 값이 표에서 하나도 없으면, 그냥 0을 반환한다.
		System.out.println("ser:"+ getTodo(tdId));
		progress = 0.0;
		System.out.println("ser.progress:tdlist_detail 표에 값이 없음");
		
	}else {
		//else인 경우에 아래 tc를 실행한다
		try {
			double complete = tdMap.getComplete(tdId); //완료한 것만 가져옴
			double total = tdMap.getTotal(tdId); //전체 리스트를 가져옴
			
			progress = complete/total;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	return progress;
}

@Override
public List<SubTodoListDTO> getMemo(int tdId) {
	List<SubTodoListDTO> list = new ArrayList<SubTodoListDTO>();
	try {
		list = tlMap.getMemo(tdId);
		//System.out.println("ser:"+ list);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return list;
}

@Override
public int memoWrite(TodoListDTO listDto) {
	int result = 0;
	try {
		result = tlMap.memoWrite(listDto);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}

@Override
public String dateSearch(int tdId) {
	
	String date = null;
	try {
		date = tlMap.dateSearch(tdId);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return date;
}

@Override
public int regiProgress(int tdId, double progress) {
	int result = 0;
	
	try {
		result = tlMap.regiProgress(tdId, progress);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return 0;
}
@Override
public int getTdDetailId(String tdDetail, int tdId) {
	List<Integer> tdDetailId = tdMap.getTdDetailId(tdDetail, tdId);
	//똑같은 값이 중복되어 있더라도 가장 최신값을 반환할 수 있도록 코드를 작성함
	return tdDetailId.get(0);
}



@Override
public int saveArchive() {
	
	String yesterday = checkToday().get("yesterdayStr"); //어제날짜를 yyMMdd로 변환
	ArchiveDTO archive = saMap.archiveCheck(yesterday);
	System.out.println("service impl archive값?" + archive);
	int result = 0;
	if(archive== null) {
		TodoListDTO todolist = saMap.getTodoList(yesterday); //기존 값을 가져와서 todolist에 담은 다음
		System.out.println("service impl dto값?"+ todolist.getTdDate());
		try {
			result = saMap.toArchive(todolist);//list에 담은 어제 값을 archive로 저장하는 기능
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("service Impl 성공여부" + result);
	}else {
		result = 2; //기존에 값이 있어서 실행될 필요가 없으면 결과값이 2로 반환됨
	}
	System.out.println("result: "+ result);
	return result;
}
@Override
@Transactional
public int saveArchiveDetail() {
   int result = 0;
   //
   try {
      String yesterday = checkToday().get("yesterdayStr"); //어제날짜를 yyMMdd로 변환
      //date 기반으로 tdId 가져오기, 근데 중복되는 값은 안가져올 수 있도록
      Set<Integer> uiqueTdIds = new HashSet<>(saMap.tdIdSearch(yesterday));
      List<Integer> tdIds = saMap.tdIdSearch(yesterday); 
      //System.out.println(tdIds.get(1));
      Set<Integer> uniqueTdIds = new HashSet<>(tdIds);//중복값은 저장하지 않도록 SET을 돌려주고
      List<Integer> uniTdIds = new ArrayList<>(uniqueTdIds); //다시 리스트로 변환함
      //그럼 이제 담기는 값은 tdId 숫자들이 담길 것임
      
      
      //System.out.println("service impl 어제날짜 해당하는 tdId값: " + uniTdIds.get(0));
      
      List<TDdetailDTO> toSaveDetail = new ArrayList<TDdetailDTO>();
      //archDetail에 저장할 값을 담아두기 위한 LIst 생성
         
      for(int i =0 ; i<uniTdIds.size(); i++) { //인덱스번호 순회해가며 tdId에 대한 tdDetail 테이블 값을 찾아서 넣어둔다.
          toSaveDetail.addAll(saMap.todoDetailCheck(uniTdIds.get(i)));
      }
      
      System.out.println("ser impl tdDetail 첫번째 값? :" + toSaveDetail.get(0).getTdDetail());
      List<TDdetailDTO> checkExist = new ArrayList<>();
      for (int i = 0; i < toSaveDetail.size(); i++) {
         checkExist.clear();
         checkExist = saMap.checkExist(toSaveDetail.get(i).getTdDetailId());
         
         if (checkExist.isEmpty()) {
            int archiveId = saMap.getArchId(yesterday); // 날짜 기반으로 archiveId 가져옴
            toSaveDetail.get(i).setTdId(archiveId);
            result = saMap.toArchiveDetail(toSaveDetail.get(i));

            // 삽입 결과 확인
            if (result == 0) {
                System.out.println("데이터 삽입 실패: " + toSaveDetail.get(i).getTdDetailId());
            } else {
                System.out.println("데이터 삽입 성공: " + toSaveDetail.get(i).getTdDetailId());
            }
         }
      }
   } catch (Exception e) {
      e.printStackTrace();
      System.out.println("오류발생");
      throw e;
   }
   
   
   return result;
}





}
