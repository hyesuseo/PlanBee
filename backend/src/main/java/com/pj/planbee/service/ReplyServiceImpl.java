package com.pj.planbee.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.planbee.dto.ReplyDTO;
import com.pj.planbee.mapper.ReplyMapper;

@Service
public class ReplyServiceImpl implements ReplyService {

	@Autowired
	ReplyMapper rm;

	// 댓글 및 대댓글 작성
	@Override
	public int addReply(ReplyDTO reply) {
		int result = 0;
		try {
			result = rm.insertReply(reply);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 특정 게시글의 댓글 및 대댓글 불러오기
	@Override
	public List<ReplyDTO> getReplies(int postId) {
		return rm.getRepliesByPostId(postId);
	}

	// 댓글 및 대댓글 수정
	@Override
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
    public List<ReplyDTO> getReplysWithReplies(int postId) {
        // 모든 댓글 & 대댓글 조회
        List<ReplyDTO> allReplies = rm.getReplyAndRepReplyByPostId(postId);

        if (allReplies == null) {  // NULL 체크
            System.out.println("[ERROR] 댓글 목록이 NULL입니다. 빈 리스트 반환.");
            return new ArrayList<>(); // 빈 리스트 반환
        }

        // 계층 구조를 위한 Map 생성
        Map<Integer, ReplyDTO> replyMap = new HashMap<>();
        List<ReplyDTO> topLevelReplies = new ArrayList<>();

        for (ReplyDTO reply : allReplies) {
            replyMap.put(reply.getReplyId(), reply);
        }

        // 댓글 & 대댓글 계층 구조 구성
        for (ReplyDTO reply : allReplies) {
            if (reply.getRepReplyId() == null) {
                topLevelReplies.add(reply); // 부모가 없는 경우 = 최상위 댓글
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

        return topLevelReplies; // 최상위 댓글 리스트 반환 (대댓글 포함됨)
    }
	
}
