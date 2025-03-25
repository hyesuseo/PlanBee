package com.pj.planbee.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.pj.planbee.dto.TDdetailDTO;
import com.pj.planbee.dto.TodoListDTO;
import com.pj.planbee.dto.TodoListDTO.SubTodoListDTO;

public interface TodoListService {
	
	
	public HashMap<String, String> checkToday();//날짜 확인하여 오늘, 내일 날짜값을 String 변환
	public int checkRow(String tdDate, String sessionId); //todolist 호출했을때 열이 있는지 확인해서 값을 반환
	//public void inputRow(String tdDate, String sessionId);//한 열을 만드는 기능
	public int tdIdSearch(String tdDate, String sessionId); //날짜, id에 해당하는 tdId 고유값 찾는 메소드
	public String dateSearch(int tdId); //tdId로 tdDate가져오는 기능
	
	//todolist에 대한 기본 기능
	//public List<TDdetailDTO> getList(); //전체의 투두리스트 가져오는 기능 테스트용
	public List<TDdetailDTO> getTodo(int tdId); //하루의 투두리스트 가져오는 기능
	public int todoWrite(TDdetailDTO dto); //투두리스트 입력하는 기능
	public int updateState(int tdDetailId, boolean state); //완료상황 t/f 업데이트하는 기능
	public int todoModify(TDdetailDTO dto); //투두리스트 자체를 수정하는 기능
	public int todoDel(int tdDetailId); //투두리스트 한 개 삭제하는 기능
	public double todoProgress(int tdId); //진척도 가져오는 기능
	public int regiProgress(int tdId, double progress);//업데이트된 진척도를 저장하는 기능
	public int getTdDetailId(String tdDetail, int tdId);//새로 입력된 것 바탕으로 tdDetailId 반환(FE 요청)
	
	
	//todolist의 memo에 대한 기능들
	public List<SubTodoListDTO> getMemo(int tdId); //하루의 메모 가져오는 기능, 하나 밖에 없으므로 string으로 받아옴
	public int memoWrite(TodoListDTO listDto);//메모를 작성하고 수정하는 기능
	//메모를 삭제하는 기능은 사용하지 않을 예정이라 삭제함
	
	
	//12시가 되면 todolist를 archive로 옮기는 기능들
	public int saveArchive();
	public int saveArchiveDetail();
	

}