package com.pj.planbee.dto;

public class PostListDTO {
	
    int postId;
    int postHit;
    String postTitle;
    String userId;
    String postDate;

    public PostListDTO() {}

    public PostListDTO(int postId, int postHit, String postTitle, String userId, String postDate) {
        this.postId = postId;
        this.postHit = postHit;
        this.postTitle = postTitle;
        this.userId = userId;
        this.postDate = postDate;
    }

    public int getPostId() {
        return postId;
    }
    public void setPostId(int postId) {
        this.postId = postId;
    }
    public int getPostHit() {
        return postHit;
    }
    public void setPostHit(int postHit) {
        this.postHit = postHit;
    }
    public String getPostTitle() {
        return postTitle;
    }
    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPostDate() {
        return postDate;
    }
    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }
}
