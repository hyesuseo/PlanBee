package com.pj.planbee.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.planbee.dto.CalendarDTO;
import com.pj.planbee.dto.ProgressDTO;
import com.pj.planbee.dto.TDstartDTO;
import com.pj.planbee.mapper.CalendarMapper;
import com.pj.planbee.mapper.TodoListMapper;


@Service
public class CalendarServiceImpl implements CalendarService {

	@Autowired TodoListMapper tlMap;
	@Autowired CalendarMapper calMap;
	
	   @Override
	   public double getProgress(String calDate, String sessionId) {
		    Double progress = tlMap.getProgress(calDate, sessionId);
		    return (progress != null) ? progress : 0.0; // 값이 없으면 0.0 반환
		}

	
	public int tdIdSearch(String tdDate, String sessionId) { //날짜와 아이디에 해당하는 tdId를 써치하는 메소드
		   List<TDstartDTO> dateId = tlMap.getDate(sessionId);
		   int selectedtdId = 0;
		   for (int i =0; i<dateId.size(); i++) {//dateId 리스트를 순회하며,todayStr과 같은 날짜가 있는지 확인 
		      if(dateId.get(i).getTodo_date().equals(tdDate)) {
		         //리스트 중에 입력한 날짜와 같은 열, 세션아이디와 같은 값을 가진 열을 찾으면 그 고유번호를 반환함
		         selectedtdId = dateId.get(i).getTodo_Id(); //for문 사용하여 index번화 반환하므로 1 더해줌
		      }
		   }
		   return selectedtdId;
		
		}
	
	    
	@Override // 현재 연속 달성일 및 최대 연속 달성일 계산
	@Transactional
    public Map<String, Integer> curProgress(String userId) {
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String todayStr = today.format(formatter);

        List<Double> userProgress = tlMap.userProgress(userId);

        int tempStreak = 0;
        int maxStreak = 0;
        int curStreak = 0;

        for (double progress : userProgress) {
            if (progress > 0.7) {
                tempStreak++;
                maxStreak = Math.max(maxStreak, tempStreak);
            } else {
                tempStreak = 0;
            }
        }

        if (!userProgress.isEmpty() && userProgress.get(userProgress.size() - 1) > 0.3) {
            curStreak = tempStreak;
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("curStreak", curStreak);
        result.put("maxStreak", maxStreak);
        return result;
    }


	@Override
	public List<ProgressDTO> getMemo(String yyMM, String sessionId) {
	    List<ProgressDTO> cal = calMap.getMemo(yyMM, sessionId);
	    return cal;
	}

	@Override
	public int addMemo(CalendarDTO calendar) {
	    try {
	        int result = calMap.addMemo(calendar); // DB에 삽입 실행
	        return (result > 0) ? 1 : 0; // 성공하면 1, 실패하면 0 반환
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0;
	    }
	}

	@Override
	public int modiMemo(CalendarDTO calendar) {
	    try {
	        int result = calMap.modiMemo(calendar); // DB에서 업데이트 실행
	        return (result > 0) ? 1 : 0; // 성공하면 1, 실패하면 0 반환
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 0;
	    }
	}

	
	@Override
	public int delMemo(int calId, int fieldNo) {
		Map<String, Object> params = new HashMap<>();
	    params.put("calId", calId);
	    params.put("fieldNo", fieldNo);
	    return calMap.delMemo(params);
		    }

	@Override
	@Transactional
	public void checkMonthly(int year, int month, String userId) {
		String monthPre = String.format("%02d%02d", year % 100, month); //yyMM형식
		int count = calMap.countByMonth(monthPre, userId); //현재 사용자의 해당 월 데이터 개수 확인
		if (count > 0) {
			return ;
	}
		//해당 월의 총 일수
		YearMonth yearMonth = YearMonth.of(year, month);
		int dayInMonth =  yearMonth.lengthOfMonth();
		
		//새로 삽입할 데이터를 저장할 리스트 생성
		List<CalendarDTO> newDate = new ArrayList<>();
		
		//1일부터 마지막 날 까지 yyMMdd형식으로 날짜 생성 후 리스트에 추가
		for(int day = 1; day <= dayInMonth; day++) {
			String calDate = String.format("%02d%02d%02d", year % 100, month, day);
			
			//CalendarDTO객체 생성 후 기본 값 설정
			CalendarDTO newEntry = new CalendarDTO();
			newEntry.setUserId(userId);
			newEntry.setCalDate(calDate);
			newEntry.setCalDetail1(null);
			newEntry.setCalDetail2(null);//기본 메모값 null
			newEntry.setCalDetail3(null); //기본 상태를 0으로 둠
			
			//리스트에 추가
			newDate.add(newEntry);
		}
		//insert 삽입 실행하여 DB에 한 번에 저장
		if(!newDate.isEmpty()) {
			calMap.insertNewDate(newDate);
		}
	}

	@Override
	public List<CalendarDTO> getMonthly(int year, int month, String userId, String fileterId) {
		//데이터가 존재하지 않으면 자동생성
		checkMonthly(year, month, userId);
		//yyMM 형식의 월 정보 생성
		String monthPre = String.format("%02d%02d", year % 100, month);
		//해당 월의 데이터 리스트 반환
		return calMap.getByMonth(monthPre, userId);



	}

	// 해당 월의 평균 진척도
	@Override
	public double monthProgress(@Param("yyMM") String yyMM, @Param("sessionId") String sessionId) {
		  return calMap.monthProgress(yyMM, sessionId);
		 
	}
	
}
