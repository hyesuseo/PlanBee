package com.pj.planbee.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.planbee.dto.GroupDTO;
import com.pj.planbee.mapper.GroupMapper;

@Service
public class GroupServiceImpl implements GroupService {
	 	@Autowired GroupMapper mapper;

	    @Override
	    public List<GroupDTO> getAllGroups() {
	        return mapper.getAllGroups();
	    }

	    @Override
	    @Transactional
	    public int joinGroup(String userId, int groupId) {
	        if (mapper.isUserInGroup(userId, groupId) == 0) { // 가입 여부 체크
	            mapper.joinGroup(userId, groupId);
	            return 1; //가입 성공
	        } 
	        return 0; // 이미 가입된 경우
	    }

	    @Override
	    public int leaveGroup(String userId, int groupId) {
	        try {
	        	if (mapper.isUserInGroup(userId, groupId) > 0) { // 가입 여부 체크
		            mapper.leaveGroup(userId, groupId);
		            return 1; //탈퇴 성공
	        	}
	        	return 0; //가입되지 않은 경우
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("그룹 탈퇴 중 오류 발생");
			}
	    	
	    }
	    
	    @Override
	    public int getUserGroupId(String userId) {
	    	 if (userId == null) {
	    		 return 0; // userId가 null이면 기본값 반환
	    		 }
	    	 Integer groupId = mapper.getUserGroupId(userId);
	    	 return (groupId != null) ? groupId : 0;
	    }
	    
	    @Override
	    public String getGroupName(int groupId) {
	        return mapper.getGroupName(groupId);
	    }
}
