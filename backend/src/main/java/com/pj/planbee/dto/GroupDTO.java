package com.pj.planbee.dto;

import java.time.LocalDateTime;

public class GroupDTO {
    int groupId;
    String groupName;
    String groupKeywords;
    LocalDateTime groupDate;
    String groupDescription;
    int memberCount; // 가입한 멤버 수 추가

    // 기본 생성자
    public GroupDTO() {}

    // 전체 필드 포함한 생성자
    public GroupDTO(int groupId, String groupName, String groupKeywords, LocalDateTime groupDate, String groupDescription, int memberCount) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupKeywords = groupKeywords;
        this.groupDate = groupDate;
        this.groupDescription = groupDescription;
        this.memberCount = memberCount;
    }

    // Getter & Setter
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupKeywords() {
        return groupKeywords;
    }

    public void setGroupKeywords(String groupKeywords) {
        this.groupKeywords = groupKeywords;
    }

    public LocalDateTime getGroupDate() {
        return groupDate;
    }

    public void setGroupDate(LocalDateTime groupDate) {
        this.groupDate = groupDate;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", groupKeywords='" + groupKeywords + '\'' +
                ", groupDate=" + groupDate +
                ", groupDescription='" + groupDescription + '\'' +
                ", memberCount=" + memberCount +
                '}';
    }
}
