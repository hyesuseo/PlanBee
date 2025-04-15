package com.pj.planbee.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.planbee.dto.ReplyDTO;
import com.pj.planbee.mapper.ReplyMapper;

@Service
public class ReplyServiceImpl implements ReplyService {

	@Autowired
	ReplyMapper rm;

	// 댓글 및 대댓글 작성
	@Override
	@Transactional
	public int addReply(ReplyDTO reply) {
		int result = 0;
		try {
			result = rm.insertReply(reply);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}

	// 특정 게시글의 댓글 및 대댓글 불러오기
	@Override
	public List<ReplyDTO> getReplies(int postId) {
		try {
			return rm.getRepliesByPostId(postId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
		
	}

	// 댓글 및 대댓글 수정
	@Override
	@Transactional
	public int updateReply(ReplyDTO reply) {
		int result = 0;
		try {
			result = rm.updateReply(reply);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 댓글 및 대댓글 삭제
	@Override
	@Transactional
	public int deleteReply(int replyId, int postId, String userId) {
		int result = 0;
		try {
			result = rm.deleteReply(replyId, postId, userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 특정 게시글의 댓글 및 대댓글 계층 구조 반환
	@Override
	@Transactional
	public List<ReplyDTO> getReplysWithReplies(int postId) {
	    List<ReplyDTO> allReplies = new ArrayList<>();
	    try {
	        allReplies = rm.getReplyAndRepReplyByPostId(postId);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ArrayList<>(); // 실패 시 안전하게 빈 리스트 반환
	    }

	    Map<Integer, ReplyDTO> replyMap = new HashMap<>();
	    List<ReplyDTO> topLevelReplies = new ArrayList<>();

	    for (ReplyDTO reply : allReplies) {
	        replyMap.put(reply.getReplyId(), reply);
	    }

	    for (ReplyDTO reply : allReplies) {
	        if (reply.getRepReplyId() == null) {
	            topLevelReplies.add(reply);
	        } else {
	            ReplyDTO parent = replyMap.get(reply.getRepReplyId());
	            if (parent != null) {
	                if (parent.getReplies() == null) {
	                    parent.setReplies(new ArrayList<>());
	                }
	                parent.getReplies().add(reply);
	            }
	        }
	    }

	    return topLevelReplies;
	}

	
}
