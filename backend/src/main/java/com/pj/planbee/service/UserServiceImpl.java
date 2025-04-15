package com.pj.planbee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.planbee.dto.TempUserDTO;
import com.pj.planbee.dto.UserDTO;
import com.pj.planbee.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired UserMapper mapper;
	
	@Autowired UserService us;
	
	@Autowired TempUserService tus;
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public int regiseterUser(UserDTO user) {
	    int result = 0;

	    try {
	        if (isUserIdExists(user.getTempUserId())) {
	            return -1; // 이미 가입된 ID
	        }
	        if (isEmailExists(user.getTempUserEmail())) {
	            return -2; // 이미 가입된 이메일
	        }

	        // 인증 상태 확인
	        int verifyStatus = tus.getVerifyStatus(user.getTempUserEmail());
	        if (verifyStatus != 1) {
	            return -4; // 이메일 인증이 완료되지 않음
	        }

	        // 인증 코드 확인
	        String storedCode = tus.getTempUserCode(user.getTempUserEmail());
	        if (storedCode == null || !storedCode.equals(user.getTempUserCode())) {
	            return -3; // 인증 코드 불일치
	        }

	        // RealUser 테이블에 저장
	        result = mapper.insertUser(user);

	        // 회원가입 성공 시 TempUser 삭제
	        if (result > 0) {
	            tus.deleteTempUser(user.getTempUserEmail());
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return -99; //예외발생 코드
	    }
	    return result;
	}




	
    @Override
    public boolean isUserIdExists(String userId) {
        return mapper.countUserId(userId) > 0;
    }

    @Override
    public boolean isEmailExists(String userEmail) {
        return mapper.countUserEmail(userEmail) > 0;
    }
	
}