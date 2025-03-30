package com.pj.planbee.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.pj.planbee.dto.BoardDTO;
import com.pj.planbee.dto.GroupInfoDTO;
import com.pj.planbee.dto.PostWithReplyDTO;
import com.pj.planbee.dto.ReplyDTO;
import com.pj.planbee.service.BoardService;
import com.pj.planbee.service.GroupService;
import com.pj.planbee.service.ReplyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Api(value = "Board API", description = "그룹 내 게시글 및 댓글 관련 API")
@RestController
@RequestMapping("/groups")
public class BoardController {
    
    @Autowired
    BoardService bs;
    
    @Autowired
    GroupService gs;
    
    @Autowired
    HttpSession se;
    
    @Autowired
    ReplyService rs;
    
    @ApiOperation(value = "그룹 정보 조회", notes = "groupId를 이용해 그룹의 정보를 조회합니다.")
    @GetMapping(value = "/{groupId}", produces = "application/json; charset=utf-8")
    public GroupInfoDTO getGroupInfo(
            @ApiParam(value = "그룹 ID", required = true) @PathVariable int groupId) {
        return bs.boardGroup(groupId);
    }
    
    @ApiOperation(value = "단일 게시글 조회 및 댓글/대댓글 포함", 
                  notes = "postId를 이용해 단일 게시글을 조회하고, 해당 게시글의 댓글 및 대댓글(계층구조)을 함께 반환합니다.")
    @GetMapping(value = "/{groupId}/boards/{postId}", produces = "application/json; charset=utf-8")
    public PostWithReplyDTO getPostWithReplies(
            @ApiParam(value = "게시글 ID", required = true) @PathVariable int postId) {
        BoardDTO post = bs.getView(postId);
        List<ReplyDTO> replies = rs.getReplysWithReplies(postId);
        PostWithReplyDTO response = new PostWithReplyDTO(post, replies);
        System.out.println("결과값: " + response);
        return response;
    }
    
    @ApiOperation(value = "게시글 작성", 
                  notes = "BoardDTO 객체를 받아 게시글을 작성합니다. 작성 성공 시 새로 작성된 게시글 ID와 리다이렉트 URL을 반환합니다.")
    @PostMapping(value = "/{groupId}/boards", produces = "application/json; charset=utf-8")
    public ResponseEntity<Map<String, Object>> createPost(
            @ApiParam(value = "게시글 작성 정보 (BoardDTO)", required = true) @RequestBody BoardDTO dto,
            HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        dto.setUserId(sessionId);

        int groupId = bs.groupSearch(sessionId);
        dto.setGroupId(groupId);

        int result = bs.writePost(dto);
        Map<String, Object> response = new HashMap<>();

        if (result == 1) {
            int postId = bs.getLatestPostIdByUser(sessionId);
            response.put("message", "게시글 작성 성공");
            response.put("redirectUrl", "/planbee/groups/" + groupId + "/boards/" + postId);
            response.put("postId", postId);
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "게시글 작성 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @ApiOperation(value = "게시글 수정", 
                  notes = "BoardDTO 객체와 postId를 받아 해당 게시글을 수정합니다.")
    @PutMapping(value = "/{groupId}/boards/{postId}", produces = "application/json; charset=utf-8")
    public int updatePost(
            @ApiParam(value = "수정할 게시글 정보 (BoardDTO)", required = true) @RequestBody BoardDTO dto,
            @ApiParam(value = "게시글 ID", required = true) @PathVariable int postId,
            HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        dto.setUserId(sessionId);
        int groupId = bs.groupSearch(sessionId);
        dto.setGroupId(groupId);
        int result = bs.boardModify(dto, sessionId, postId);
        return result;
    }
    
    @ApiOperation(value = "게시글 삭제", 
                  notes = "postId와 세션 정보를 이용해 게시글을 삭제합니다.")
    @DeleteMapping(value = "/{groupId}/boards/{postId}", produces = "application/json; charset=utf-8")
    public int deletePost(
            @ApiParam(value = "게시글 ID", required = true) @PathVariable int postId,
            HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        int result = bs.boardDel(postId, sessionId);
        return result;
    }
    
    @ApiOperation(value = "게시글 조회수 증가", 
                  notes = "postId를 이용해 해당 게시글의 조회수를 증가시킵니다.")
    @PutMapping(value = "/{groupId}/boards/{postId}/hit", produces = "application/json; charset=utf-8")
    public int increasePostView(
            @ApiParam(value = "게시글 ID", required = true) @PathVariable int postId) {
        int result = bs.boardHit(postId);
        return result;
    }
    
    @ApiOperation(value = "내가 작성한 게시글 조회", 
                  notes = "userId를 통해 해당 사용자가 작성한 게시글들을 조회합니다.")
    @GetMapping(value = "/users/{userId}/boards", produces = "application/json; charset=utf-8")
    public GroupInfoDTO getUserPosts(
            @ApiParam(value = "사용자 ID", required = true) @PathVariable String userId) {
        return bs.boardUser(userId);
    }
    
    @ApiOperation(value = "게시글 검색 및 정렬", 
                  notes = "groupId와 함께 선택적으로 searchType, query, sort 파라미터를 이용해 게시글을 검색 및 정렬합니다.")
    @GetMapping(value = "/{groupId}/boards", produces = "application/json; charset=utf-8")
    public ResponseEntity<GroupInfoDTO> getSortedOrFilteredBoards(
            @ApiParam(value = "그룹 ID", required = true) @PathVariable int groupId, 
            @ApiParam(value = "검색 타입", required = false) @RequestParam(required = false) String searchType, 
            @ApiParam(value = "검색 쿼리", required = false) @RequestParam(required = false) String query, 
            @ApiParam(value = "정렬 조건", required = false) @RequestParam(required = false) String sort) {
        GroupInfoDTO result = bs.getSortedOrFilteredBoards(groupId, searchType, query, sort);
        return ResponseEntity.ok(result);
    }
    
    @ApiOperation(value = "그룹 탈퇴", 
                  notes = "groupId와 세션 정보를 이용해 사용자가 그룹을 탈퇴합니다. 탈퇴 성공 여부에 따라 리다이렉트 URL을 반환합니다.")
    @PostMapping(value = "/{groupId}/leave", produces = "application/json; charset=utf-8")
    public ResponseEntity<Map<String, Object>> leaveGroup(
            @ApiParam(value = "그룹 ID", required = true) @PathVariable int groupId,
            HttpSession session) {
        String userId = (String) session.getAttribute("sessionId");
        int success = gs.leaveGroup(userId, groupId);
        
        Map<String, Object> response = new HashMap<>();
        if (success == 1) {
            response.put("redirectUrl", "/planbee/group/list");
        } else {
            response.put("redirectUrl", "/planbee/group/" + groupId);
        }
        return ResponseEntity.ok(response);
    }
}
