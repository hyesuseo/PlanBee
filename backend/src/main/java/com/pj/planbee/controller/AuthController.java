package com.pj.planbee.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pj.planbee.dto.LoginDTO;
import com.pj.planbee.dto.TempUserDTO;
import com.pj.planbee.dto.UserDTO;
import com.pj.planbee.mapper.UserMapper;
import com.pj.planbee.service.LoginService;
import com.pj.planbee.service.TempUserService;
import com.pj.planbee.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Auth API", description = "회원가입, 로그인, 이메일 인증 등 인증 관련 API")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {

    @Autowired 
    TempUserService tempUserService;
    
    @Autowired 
    UserService userService;

    @Autowired 
    LoginService loginService;
    
    @Autowired 
    UserMapper usermapper;

    @ApiOperation(value = "이메일 인증 코드 발송", 
                  notes = "TempUserDTO 객체를 받아 이메일 인증 코드를 발송합니다. " +
                          "반환값은 결과 코드로, -1: 이미 가입된 ID, -2: 이미 가입된 이메일, " +
                          "양수: 인증 코드 발송 성공, 0: 알 수 없는 오류를 의미합니다.")
    @PostMapping(value = "/email/send", produces = "application/json; charset=utf-8")
    public int sendVerificationCode(
            @ApiParam(value = "임시 사용자 정보 (TempUserDTO)", required = true) @RequestBody TempUserDTO dto) {
        int result = tempUserService.insertOrUpdateTempUser(dto);
        if (result == -1) return result; // 이미 가입된 ID
        else if (result == -2) return result; // 이미 가입된 이메일
        else if (result > 0) return result; // 이메일 인증 코드 발송 성공
        else return 0; // 알 수 없는 오류
    }

    @ApiOperation(value = "이메일 인증 코드 확인", 
                  notes = "TempUserDTO 객체를 받아서, 저장된 인증 코드와 비교하여 인증 상태를 업데이트합니다.")
    @PostMapping(value = "/email/verify", produces = "application/json; charset=utf-8")
    public int verifyUserCode(
            @ApiParam(value = "임시 사용자 정보 (TempUserDTO) - 이메일과 인증 코드 포함", required = true) @RequestBody TempUserDTO dto) {
        
        usermapper.disableSafeUpdates();
        String storedCode = tempUserService.getTempUserCode(dto.getTempUserEmail());

        if (storedCode != null && storedCode.equals(dto.getTempUserCode())) {
            return tempUserService.updateVerifyStatus(dto.getTempUserEmail()); // 인증 성공
        } else {
            return -1; // 인증 코드 불일치
        }
    }

    @ApiOperation(value = "회원가입", 
                  notes = "UserDTO 객체를 받아 회원가입을 진행합니다. 반환값은 결과 코드로, " +
                          "-1: user_id 중복, -2: email 중복, -3: 인증 코드 불일치, -4: 인증 미완료, " +
                          "양수: 회원가입 성공을 의미합니다.")
    @PostMapping(value = "/register", produces = "application/json; charset=utf-8")
    public int registerUser(
            @ApiParam(value = "회원가입 정보 (UserDTO)", required = true) @RequestBody UserDTO user) {
        int result = userService.regiseterUser(user);
        if (result == -1) return result;
        else if (result == -2) return result;
        else if (result == -3) return result;
        else if (result == -4) return result;
        else if (result > 0) return result;
        else return 0;
    }

    @ApiOperation(value = "아이디 존재 여부 확인", 
                  notes = "PathVariable로 전달된 userId가 존재하는지 확인합니다.")
    @GetMapping(value = "/check-id/{userId}", produces = "application/json; charset=utf-8")
    public boolean checkUserId(
            @ApiParam(value = "확인할 userId", required = true) @PathVariable String userId) {
        return userService.isUserIdExists(userId);
    }

    @ApiOperation(value = "이메일 존재 여부 확인", 
                  notes = "PathVariable로 전달된 email이 존재하는지 확인합니다.")
    @GetMapping(value = "/check-email/{email}", produces = "application/json; charset=utf-8")
    public boolean checkEmail(
            @ApiParam(value = "확인할 이메일", required = true) @PathVariable String email) {
        return userService.isEmailExists(email);
    }

    @ApiOperation(value = "로그인", 
                  notes = "LoginDTO 객체를 받아 로그인 처리를 진행합니다. 로그인 성공 시 세션에 userId가 저장됩니다. " +
                          "반환값은 결과 코드로, -3: 입력값 null, -2: 아이디 미존재, -1: 비밀번호 불일치, 1: 성공을 의미합니다.")
    @PostMapping(value = "/login", produces = "application/json; charset=utf-8")
    public int login(
            @ApiParam(value = "로그인 정보 (LoginDTO: userId, userPw)", required = true) @RequestBody LoginDTO loginDTO,
            HttpSession session) {
        String userId = loginDTO.getUserId();
        String userPw = loginDTO.getUserPw();

        if (userId == null || userPw == null) {
            return -3;
        }

        boolean isUserExists = loginService.isUserExists(userId);
        if (!isUserExists) {
            return -2;
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("userPw", userPw);

        LoginDTO user = loginService.login(paramMap);
        if (user == null) {
            return -1;
        }

        session.setAttribute("sessionId", userId);
        return 1;
    }

    @ApiOperation(value = "로그아웃", 
                  notes = "현재 세션을 무효화하여 로그아웃 처리합니다.")
    @PostMapping(value = "/logout", produces = "application/json; charset=utf-8")
    public int logout(HttpSession session) {
        session.invalidate();
        return 1;
    }

    @ApiOperation(value = "로그인 상태 확인", 
                  notes = "세션에 userId가 존재하는지 확인하여 로그인 상태를 반환합니다. (1: 로그인, 0: 미로그인)")
    @GetMapping(value = "/session", produces = "application/json; charset=utf-8")
    public int checkSession(HttpSession session) {
        return (session.getAttribute("sessionId") != null) ? 1 : 0;
    }
    
    @ApiOperation(value="유저 이름 가져오기", notes="헤더에 유저 이름 띄우는 용도(ex: ~님 환영합니다)")
    @GetMapping(value="/getUserId", produces = "appliction/json; charset=utf-8")
    public String getUserId(HttpSession se) {
    	String userId = (String) se.getAttribute("sessionId");
    	return userId;
    }
}
