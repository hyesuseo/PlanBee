package com.pj.planbee.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.pj.planbee.dto.LoginDTO;
import com.pj.planbee.mapper.LoginMapper;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired LoginMapper mapper;

    @Override
    public LoginDTO login(@RequestBody Map<String, Object> paramMap) {
    	if (paramMap == null || !paramMap.containsKey("userId") || !paramMap.containsKey("userPw")) {
    		throw new IllegalArgumentException("로그인 파라미터 부족");
    	}
    	try {
    		return mapper.login(paramMap); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("로그인 처리 중 오류가 발생했습니다");
		}
        
    }

	@Override
	public boolean isUserExists(String userId) {
		try {
			return mapper.countUserId(userId) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("사용자 존재여부 확인 중 오류발생");
		}
		
	}

}