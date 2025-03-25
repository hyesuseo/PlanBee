package com.pj.planbee.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pj.planbee.dto.GroupDTO;

@Mapper
public interface GroupMapper {
	 // 그룹 목록 조회
    List<GroupDTO> getAllGroups();

    // 특정 그룹 가입 여부 확인
    public int isUserInGroup(@Param("user_id") String userId, @Param("group_id") int groupId);

    // 그룹 가입
    public int joinGroup(@Param("user_id") String userId, @Param("group_id") int groupId);

    // 그룹 탈퇴
    public int leaveGroup(@Param("user_id") String userId, @Param("group_id") int groupId);
    
    public Integer getUserGroupId(@Param("userId") String userId);  // 사용자의 그룹 ID 조회
    
    public String getGroupName(@Param("groupId") int groupId);
}
