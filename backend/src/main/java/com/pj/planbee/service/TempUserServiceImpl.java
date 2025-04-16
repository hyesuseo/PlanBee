package com.pj.planbee.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import javax.mail.internet.MimeMessage;

import com.pj.planbee.dto.TempUserDTO;
import com.pj.planbee.mapper.TempUserMapper;

@Service
public class TempUserServiceImpl implements TempUserService{
    
    @Autowired TempUserMapper mapper;
    @Autowired JavaMailSender mailSender;

    @Transactional
    public int insertOrUpdateTempUser(TempUserDTO dto) {
        int result = 0;

        try {
        	//생성된 지 5분 지난 데이터 삭제
        	mapper.deleteExpiredTempUsers();
        	 
            // ID 중복 검사 (Real_User 테이블에서 확인)
            if (mapper.countUserId(dto.getTempUserId()) > 0) {
                return -1; // 이미 가입된 ID
            }

            // Email 중복 검사 (Real_User 테이블에서 확인)
            if (mapper.countUserEmail(dto.getTempUserEmail()) > 0) {
                return -2; // 이미 가입된 이메일
            }

            // 기존 temp_user 테이블에서 이메일이 존재하는지 확인
            Integer existingUser = mapper.checkTempUserExists(dto.getTempUserEmail());

            // 6자리 인증 코드 생성
            String verificationCode = generateVerificationCode();
            dto.setTempUserCode(verificationCode);
            dto.setVerifyStatus(0); // 기본값: 인증 미완료 (false)

            // 이메일 전송 (코드 생성 후 바로 전송)
            sendCode(dto.getTempUserEmail(), verificationCode);

            if (existingUser != null && existingUser > 0) {
                // 기존 데이터가 있다면 UPDATE 실행
                result = mapper.updateTempUser(dto);
            } else {
                // 기존 데이터가 없으면 INSERT 실행
                result = mapper.insertTempUser(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("이메일 인증 실패: " + e.getMessage());
        }
        return result;
    }


    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public int sendCode(String recipientEmail, String code) {
        int result = 0;
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("PlanBee 회원가입 인증 코드");
            helper.setFrom("hyesuseo.dev@gmail.com", "PlanBee 메일 인증");

            // 이메일 본문 생성
            String htmlContent = "<html><body style='margin: 0; padding: 0;'>"
                + "<table role='presentation' width='100%' cellspacing='0' cellpadding='0' border='0' style='background-color: #000000; width: 100%; padding: 20px 0;'>"
                + "<tr><td align='center'>"
                + "<table role='presentation' width='500' cellspacing='0' cellpadding='0' border='0' style='background-color: #000000; color: #ffffff; border-radius: 10px; padding: 20px; text-align: center;'>"
                + "<tr><td>"
                + "<h1 style='color: #FFD700;'>PlanBee 회원가입 인증 코드</h1>"
                + "<p style='font-size: 16px; margin-top: 20px;'>아래의 인증 코드를 입력하여 회원가입을 완료하세요.</p>"
                + "<div style='background-color: #FFD700; margin-top:10px; color: #000000; font-size: 24px; font-weight: bold; padding: 10px; border-radius: 5px; display: inline-block; margin: 10px 0;'>"
                + code + "</div>"
                + "<p style='margin-top: 20px;'>전송 시간으로부터 5분 이내에 정확히 입력해 주세요.</p>"
                + "<p style='margin-top: 30px; font-size: 12px; color: #aaaaaa;'>이 이메일은 자동 발송 메일입니다. 회신하지 마세요.</p>"
                + "</td></tr></table>"
                + "</td></tr></table>"
                + "</body></html>";

            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            result = 1; // 이메일 전송 성공

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int deleteTempUser(String tempUserEmail) {
        return mapper.deleteTempUser(tempUserEmail);
    }

    public String getTempUserCode(String tempUserEmail) {
        return mapper.getTempUserCode(tempUserEmail);
    }

	@Override
	public List<TempUserDTO> getTempUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TempUserDTO> getTempUserData(String tempUserId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public TempUserDTO getTempUserByEmail(String tempUserEmail) {
	    return mapper.getTempUserByEmail(tempUserEmail);
	}

	@Override
	public int insertTempUser(TempUserDTO dto) {
		// TODO Auto-generated method stub
		return mapper.insertTempUser(dto);
	}

	@Override
	public int updateVerifyStatus(String tempUserEmail) {
		// TODO Auto-generated method stub
		return mapper.updateVerifyStatus(tempUserEmail);
	}
	
	@Override
	public Integer getVerifyStatus(String tempUserEmail) {
	    Integer status = mapper.getVerifyStatus(tempUserEmail);
	    return (status != null) ? status : 0; // NULL이면 기본값 0 반환
	}
	
}