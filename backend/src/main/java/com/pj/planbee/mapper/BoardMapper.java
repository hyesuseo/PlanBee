package com.pj.planbee.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.pj.planbee.dto.BoardDTO;
import com.pj.planbee.dto.PostListDTO;

public interface BoardMapper {
	public List<BoardDTO> getAllList();
	public List<PostListDTO> getAllPost(int groupId);
	public BoardDTO getView(int postId);
	public int writePost(BoardDTO dto);
	public int boardModify(BoardDTO dto);
	public int boardDel(int PostId);
	public int boardHit(int postId);
	public List<PostListDTO> boardGroup(int groupId);
	public String getWriter(int postId); //postId기반으로 글쓴이 누구인지 확인
	public List<BoardDTO> boardMine(String sessionId);
	public List<PostListDTO> boardUser(String userId);
	public List<PostListDTO> maxHit(int groupId);
	public int groupSearch(String sessionId); 
	public List<PostListDTO> contentSearch(@Param("groupId") int groupId,@Param("content") String content);
	public List<PostListDTO> titleSearch(@Param("groupId") int groupId,@Param("content") String content);
	public List<PostListDTO> titleAndContentSearch(@Param("groupId") int groupId, @Param("content") String content);
	public List<PostListDTO> newestSort(int groupId); // 최신 순 정렬
	public List<PostListDTO> oldestSort(int groupId); // 오래된 순 정렬
	public String getGroupName(int groupId);
	public int getGroupMemberCount(int groupId);
	public String getGroupNameWithUserId(String userId);
	public int getGroupMemberCountWithUserId(String userId);
	public int insertPost(BoardDTO dto);
	public int getLatestPostIdByUser(String userId);
}
