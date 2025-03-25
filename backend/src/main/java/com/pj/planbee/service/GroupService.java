package com.pj.planbee.service;

import java.util.List;

import com.pj.planbee.dto.GroupDTO;

public interface GroupService {
    List<GroupDTO> getAllGroups(); // 그룹 목록 조회
    public int joinGroup(String userId, int groupId); // 그룹 가입
    public int leaveGroup(String userId, int groupId); // 그룹 탈퇴
    public int getUserGroupId(String userId);  // 사용자가 가입한 그룹 ID 조회
    public String getGroupName(int groupId);   // 그룹 이름
}
