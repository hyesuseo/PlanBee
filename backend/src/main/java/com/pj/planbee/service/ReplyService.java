package com.pj.planbee.service;

import java.util.List;

import com.pj.planbee.dto.ReplyDTO;

public interface ReplyService {
	int addReply(ReplyDTO reply);
    List<ReplyDTO> getReplies(int postId);
    int updateReply(ReplyDTO reply);
    int deleteReply(int replyId, int postId, String userId);
	List<ReplyDTO> getReplysWithReplies(int postId);
}
