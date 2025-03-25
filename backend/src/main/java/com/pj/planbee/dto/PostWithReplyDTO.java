package com.pj.planbee.dto;

import java.util.List;

public class PostWithReplyDTO {
    private BoardDTO post;
    private List<ReplyDTO> replies; 

    // 기본 생성자
    public PostWithReplyDTO() {}

    // 매개변수 생성자
    public PostWithReplyDTO(BoardDTO post, List<ReplyDTO> replies) {
        this.post = post;
        this.replies = replies;
    }

    public BoardDTO getPost() { return post; }
    public void setPost(BoardDTO post) { this.post = post; }

    public List<ReplyDTO> getReplies() { return replies; } // 변경 완료
    public void setReplies(List<ReplyDTO> replies) { this.replies = replies; } // 변경 완료

    @Override
    public String toString() {
        return "PostWithReplyDTO{" +
                "post=" + post +
                ", replies=" + replies + // 변경 완료
                '}';
    }
}
