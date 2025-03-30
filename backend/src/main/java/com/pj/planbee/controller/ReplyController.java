package com.pj.planbee.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pj.planbee.dto.ReplyDTO;
import com.pj.planbee.service.ReplyService;

@RestController
@RequestMapping("/groups/{groupId}/boards/{postId}/reply")
public class ReplyController {
	
	@Autowired ReplyService rs;
	@Autowired HttpSession se;
    
	// 댓글 작성 (postId를 URL에서 가져와 DTO에 설정 + 세션에서 userId 가져오기)
    @PostMapping(value = "", produces = "application/json; charset=utf-8")
    public ResponseEntity<Integer> createReply(@PathVariable int postId, @RequestBody ReplyDTO reply) {
    	
        String userId = (String) se.getAttribute("sessionId"); // 현재 로그인한 사용자의 ID 가져오기
        
        reply.setPostId(postId);
        reply.setUserId(userId); // 세션에서 가져온 userId 설정

        int result = rs.addReply(reply);
        return ResponseEntity.ok(result);
    }

    // 대댓글 작성 (부모 댓글의 ID를 받아서 저장)
    @PostMapping(value = "/{repReplyId}", produces = "application/json; charset=utf-8")
    public ResponseEntity<Integer> createReply(@PathVariable int postId, @PathVariable int repReplyId, @RequestBody ReplyDTO reply) {

        String userId = (String) se.getAttribute("sessionId"); // 현재 로그인한 사용자의 ID 가져오기

        reply.setPostId(postId);
        reply.setUserId(userId);
        reply.setRepReplyId(repReplyId); // 부모 댓글 ID 설정

        int result = rs.addReply(reply);
        return ResponseEntity.ok(result);
    }

    // 댓글/대댓글 수정
    @PutMapping(value="/{replyId}", produces = "application/json; charset=utf-8")
    public ResponseEntity<Integer> updateReply(@PathVariable int postId, @PathVariable int replyId, @RequestBody ReplyDTO reply) {

        String userId = (String) se.getAttribute("sessionId");


        reply.setReplyId(replyId);
        reply.setPostId(postId);  // postId 설정 추가!
        reply.setUserId(userId);

        int result = rs.updateReply(reply);

        return ResponseEntity.ok(result);
    }

    // 댓글/대댓글 삭제
    @DeleteMapping(value="/{replyId}", produces = "application/json; charset=utf-8")
    public ResponseEntity<Integer> deleteReply(@PathVariable int postId, @PathVariable int replyId) {

        String userId = (String) se.getAttribute("sessionId");
  
        int result = rs.deleteReply(replyId, postId, userId);  // postId와 userId 추가

        return ResponseEntity.ok(result);
    }
}
