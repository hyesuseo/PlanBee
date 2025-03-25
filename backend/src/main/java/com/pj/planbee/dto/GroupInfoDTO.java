package com.pj.planbee.dto;

import java.util.List;

public class GroupInfoDTO {
    private String groupName;
    private int groupMemberCount;
    private List<PostListDTO> posts; // 게시글 목록

    public GroupInfoDTO() {}

    public GroupInfoDTO(String groupName, int groupMemberCount, List<PostListDTO> posts) {
        this.groupName = groupName;
        this.groupMemberCount = groupMemberCount;
        this.posts = posts;
    }

    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public int getGroupMemberCount() {
        return groupMemberCount;
    }
    public void setGroupMemberCount(int groupMemberCount) {
        this.groupMemberCount = groupMemberCount;
    }
    public List<PostListDTO> getPosts() {
        return posts;
    }
    public void setPosts(List<PostListDTO> posts) {
        this.posts = posts;
    }
}
