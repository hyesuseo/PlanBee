package com.pj.planbee.service;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.planbee.dto.BoardDTO;
import com.pj.planbee.dto.GroupInfoDTO;
import com.pj.planbee.dto.PostListDTO;
import com.pj.planbee.mapper.BoardMapper;

@Service
public class BoardServiceImpl implements BoardService{
	@Autowired BoardMapper btMap;

	//모든 게시글 리스트
	@Override
	public List<BoardDTO> getAllList() {
		List<BoardDTO> totalBoard = new ArrayList<BoardDTO>();
		
		try {
			totalBoard = btMap.getAllList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalBoard;
	}

	//게시글 보기
	@Override
	public BoardDTO getView(int postId) {
		
		BoardDTO dto = null;
		try {
			dto = btMap.getView(postId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}

	//게시글 작성
	@Override
	public int writePost(BoardDTO dto) {
		int result = 0;
		try {
			result = btMap.writePost(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	//게시글 수정
	@Override
	public int boardModify(BoardDTO dto, String sessionId, int postId) {
		int result = 0;
		String writer = btMap.getWriter(postId);
		dto.setPostId(postId);
		if(writer.equals(sessionId)) {
			try {
				result = btMap.boardModify(dto);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			result = -1;
		}
		
		return result;
	}

	// 게시글 삭제
	@Override
	public int boardDel(int postId, String sessionId) {
		String writer = btMap.getWriter(postId);
		int result = 0; 
		if(writer.equals(sessionId)) {
			try {
				result = btMap.boardDel(postId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			result = -1;
		}
		return result;
	}

	
	// 조회수 증가
	@Override
	public int boardHit(int postId) {
		int result = 0;
		try {
			result = btMap.boardHit(postId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 게시글이랑 같이 보낼 그룹 정보
	@Override
	public GroupInfoDTO boardGroup(int groupId) {
	    List<PostListDTO> posts = new ArrayList<>();
	    String groupName = "";
	    int groupMemberCount = 0;

	    try {
	        // 1. 게시글 목록 조회
	        posts = btMap.boardGroup(groupId);
	        
	        // 2. 그룹 이름 조회
	        groupName = btMap.getGroupName(groupId);
	        
	        // 3. 그룹 인원 수 조회
	        groupMemberCount = btMap.getGroupMemberCount(groupId);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return new GroupInfoDTO(groupName, groupMemberCount, posts);
	}

	// 내가 쓴 글 조회
	@Override
	public GroupInfoDTO boardUser(String userId) {
		List<PostListDTO> posts = new ArrayList<>();
	    String groupName = "";
	    int groupMemberCount = 0;

	    try {
	        // 1. 게시글 목록 조회
	        posts = btMap.boardUser(userId);
	        
	        // 2. 그룹 이름 조회
	        groupName = btMap.getGroupNameWithUserId(userId);
	        
	        // 3. 그룹 인원 수 조회
	        groupMemberCount = btMap.getGroupMemberCountWithUserId(userId);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return new GroupInfoDTO(groupName, groupMemberCount, posts);
	}
	
	// 조회 수 많은 순 정렬
	@Override
	public GroupInfoDTO maxHit(int groupId) {
		 List<PostListDTO> posts = new ArrayList<>();
		    String groupName = "";
		    int groupMemberCount = 0;

		    try {
		        // 1. 게시글 목록 조회
		        posts = btMap.maxHit(groupId);
		        
		        // 2. 그룹 이름 조회
		        groupName = btMap.getGroupName(groupId);
		        
		        // 3. 그룹 인원 수 조회
		        groupMemberCount = btMap.getGroupMemberCount(groupId);

		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    return new GroupInfoDTO(groupName, groupMemberCount, posts);
	}
	
	// 최신 순 정렬
	@Override
	public GroupInfoDTO newestSort(int groupId){
		 List<PostListDTO> posts = new ArrayList<>();
		    String groupName = "";
		    int groupMemberCount = 0;

		    try {
		        // 1. 게시글 목록 조회
		        posts = btMap.newestSort(groupId);
		        
		        // 2. 그룹 이름 조회
		        groupName = btMap.getGroupName(groupId);
		        
		        // 3. 그룹 인원 수 조회
		        groupMemberCount = btMap.getGroupMemberCount(groupId);

		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    return new GroupInfoDTO(groupName, groupMemberCount, posts);
	}
	
	// 오래된 순 정렬
	@Override
	public GroupInfoDTO oldestSort(int groupId){
		 List<PostListDTO> posts = new ArrayList<>();
		    String groupName = "";
		    int groupMemberCount = 0;

		    try {
		        // 1. 게시글 목록 조회
		        posts = btMap.oldestSort(groupId);
		        
		        // 2. 그룹 이름 조회
		        groupName = btMap.getGroupName(groupId);
		        
		        // 3. 그룹 인원 수 조회
		        groupMemberCount = btMap.getGroupMemberCount(groupId);

		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    return new GroupInfoDTO(groupName, groupMemberCount, posts);
	}

	// 그룹 아이디 가져오기
	@Override
	public int groupSearch(String sessionId) {
		int groupId = btMap.groupSearch(sessionId);
		return groupId;
	}

	// 내용 검색
	@Override
	public GroupInfoDTO contentSearch(int groupId, String content) {
		List<PostListDTO> posts = new ArrayList<>();
	    String groupName = "";
	    int groupMemberCount = 0;

	    try {
	        // 1. 게시글 목록 조회
	        posts = btMap.contentSearch(groupId, content);
	        
	        // 2. 그룹 이름 조회
	        groupName = btMap.getGroupName(groupId);
	        
	        // 3. 그룹 인원 수 조회
	        groupMemberCount = btMap.getGroupMemberCount(groupId);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return new GroupInfoDTO(groupName, groupMemberCount, posts);
	}

	// 제목 검색
	@Override
	public GroupInfoDTO titleSearch(int groupId, String content) {
		List<PostListDTO> posts = new ArrayList<>();
	    String groupName = "";
	    int groupMemberCount = 0;

	    try {
	        // 1. 게시글 목록 조회
	        posts = btMap.titleSearch(groupId, content);
	        
	        // 2. 그룹 이름 조회
	        groupName = btMap.getGroupName(groupId);
	        
	        // 3. 그룹 인원 수 조회
	        groupMemberCount = btMap.getGroupMemberCount(groupId);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return new GroupInfoDTO(groupName, groupMemberCount, posts);
	}
	
	// 제목 + 내용 검색
	@Override
	public GroupInfoDTO titleAndContentSearch(int groupId, String content) {
	    List<PostListDTO> posts = new ArrayList<>();
	    String groupName = "";
	    int groupMemberCount = 0;

	    try {
	        posts = btMap.titleAndContentSearch(groupId, content);
	        groupName = btMap.getGroupName(groupId);
	        groupMemberCount = btMap.getGroupMemberCount(groupId);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return new GroupInfoDTO(groupName, groupMemberCount, posts);
	}

	
	@Override
	public GroupInfoDTO getSortedOrFilteredBoards(int groupId, String searchType, String query, String sort) {
	    List<PostListDTO> posts = new ArrayList<>();
	    String groupName = "";
	    int groupMemberCount = 0;

	    try {
	        // 검색 우선 처리
	        if (searchType != null && query != null && !query.trim().isEmpty()) {
	            switch (searchType.toLowerCase()) {
	                case "content":
	                    posts = btMap.contentSearch(groupId, query);
	                    break;
	                case "title":
	                    posts = btMap.titleSearch(groupId, query);
	                    break;
	                case "title_content":
	                    posts = btMap.titleAndContentSearch(groupId, query);
	                    break;
	                default:
	                    posts = btMap.getAllPost(groupId); // fallback
	            }
	        } else {
	            // 검색이 없는 경우 전체 게시글
	            posts = btMap.getAllPost(groupId);
	        }

	        // 정렬은 검색된 결과에 대해 직접 정렬
	        if (sort != null && !posts.isEmpty()) {
	            switch (sort.toLowerCase()) {
	                case "hit":
	                    posts.sort((a, b) -> Integer.compare(b.getPostHit(), a.getPostHit())); // 조회수 내림차순
	                    break;
	                case "newest":
	                    posts.sort((a, b) -> b.getPostDate().compareTo(a.getPostDate())); // 최신순
	                    break;
	                case "oldest":
	                    posts.sort((a, b) -> a.getPostDate().compareTo(b.getPostDate())); // 오래된순
	                    break;
	            }
	        }

	        // 그룹 정보 조회
	        groupName = btMap.getGroupName(groupId);
	        groupMemberCount = btMap.getGroupMemberCount(groupId);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return new GroupInfoDTO(groupName, groupMemberCount, posts == null ? new ArrayList<>() : posts);
	}

	
	// 해당 유저가 작성한 가장 최신의 글
	@Override
    public int getLatestPostIdByUser(String userId) {
        return btMap.getLatestPostIdByUser(userId);
    }
}
