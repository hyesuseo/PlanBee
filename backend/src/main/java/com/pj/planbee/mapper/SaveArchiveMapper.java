package com.pj.planbee.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.pj.planbee.dto.ArchDetailDTO;
import com.pj.planbee.dto.ArchiveDTO;
import com.pj.planbee.dto.TDdetailDTO;
import com.pj.planbee.dto.TodoListDTO;

public interface SaveArchiveMapper {

	public TodoListDTO getTodoList(String yesterday); //어제 날짜 기반으로 전체 투두리스트 정보 가져오는 기능
	
	public int toArchive(TodoListDTO todolist);
	
	public ArchiveDTO archiveCheck(String yesterday); 
	
	public List<Integer> tdIdSearch(String yesterday);
	
	public int getArchId(String yesterday);
	
	public ArrayList<TDdetailDTO> todoDetailCheck(@Param("tdIds")int tdIds);
	
	public ArrayList<TDdetailDTO> checkExist(int tdDetailId);
	
	public int toArchiveDetail(TDdetailDTO tododetail);
	
	
}
