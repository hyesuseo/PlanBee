package com.pj.planbee.service;

import java.util.List;



import com.pj.planbee.dto.BoardDTO;


public interface BoardService {
	public List<BoardDTO> getAllList();  //모든 게시글 가져오는 기능
	public BoardDTO getView(int postId); //하나의 게시글 가져오는 기능
	public int writePost(BoardDTO dto); //글을 쓰는 기능
	public int boardModify(BoardDTO dto, String sessionId, int postId); //글을 수정하는 기능
	public int boardDel(int postId, String sessionId); //글을 삭제하는 기능
	public int boardHit(int postId); //글 클릭하면 조회수 올라가는 기능
	public List<BoardDTO> boardGroup(int groupId); //그룹별 글 조회기능
	public List<BoardDTO> boardUser(String userId); //아이디로 글 조회기능
	public List<BoardDTO> maxHit(int groupId);//조회수 최대조회 기능
	public int groupSearch(String sessionId); //세션아이디 기반으로 그룹번호 가져오는 기능
	public List<BoardDTO> contentSearch(int groupId, String content);//content 키로 내용서치
	public List<BoardDTO> titleSearch(int groupId, String content);//content키로 제목 서치
}
