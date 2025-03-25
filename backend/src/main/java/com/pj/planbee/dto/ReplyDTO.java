package com.pj.planbee.dto;

import java.util.List;

public class ReplyDTO {
	
	int replyId, postId;
    String userId, replyContent, replyDate;
    Integer repReplyId;  // 부모 댓글 ID (null 가능)
    List<ReplyDTO> replies;

    public ReplyDTO() {}

    public ReplyDTO(int replyId, int postId, String userId, String replyContent, String replyDate, Integer repReplyId) {
        this.replyId = replyId;
        this.postId = postId;
        this.userId = userId;
        this.replyContent = replyContent;
        this.replyDate = replyDate;
        this.repReplyId = repReplyId;
    }

	public int getReplyId() {
		return replyId;
	}

	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public String getReplyDate() {
		return replyDate;
	}

	public void setReplyDate(String replyDate) {
		this.replyDate = replyDate;
	}

	public Integer getRepReplyId() {
		return repReplyId;
	}

	public void setRepReplyId(Integer repReplyId) {
		this.repReplyId = repReplyId;
	}
	
	public List<ReplyDTO> getReplies() { return replies; }
    public void setReplies(List<ReplyDTO> replies) { this.replies = replies; }
    
}
