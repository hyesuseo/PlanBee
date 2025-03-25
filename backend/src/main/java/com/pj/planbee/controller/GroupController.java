package com.pj.planbee.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.pj.planbee.dto.GroupDTO;
import com.pj.planbee.service.GroupService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders= "*", allowCredentials = "true")
@RequestMapping("/groups")
public class GroupController {
	
    @Autowired GroupService gs;
    @Autowired HttpSession se;
    

    //세션 생성 메소드(로그인 연결시 삭제 예정)
  	@PostMapping(value = "/makeSession", produces = "application/json; charset=utf-8")
  	public String session() {
  		se.setAttribute("sessionId", "팥붕");
  		return (String) se.getAttribute("sessionId");
  	}
  	
  	// 로그아웃(로그인 연결시 삭제 예정)
  	@PostMapping(value = "/logout", produces = "application/json; charset=utf-8")
  	public int logout() {
  		se.invalidate();
  		return 1; // 로그아웃 성공
  	}
  	
  	// 로그인 상태 확인(로그인 연결시 삭제 예정)
  	@GetMapping(value = "/checkSession", produces = "application/json; charset=utf-8")
  	public int checkSession() {
  		return (se.getAttribute("sessionId") != null) ? 1 : 0;  // 1: 로그인된 상태, 0: 로그인되지 않음
  	}


  	// 주소 : http://localhost:8080/planbee/group
  	// 사용자가 가입한 그룹 확인 후 리다이렉트
  	@GetMapping(value="", produces = "application/json; charset=utf-8")
  	public ResponseEntity<Map<String, Object>> checkUserGroup() {
  	    String userId = (String) se.getAttribute("sessionId");
  	    Integer groupId = gs.getUserGroupId(userId);
  	    System.out.println("groupId"+ groupId);
  	    Map<String, Object> response = new HashMap<>();

  	    if (groupId == null || groupId == 0) {
  	        response.put("redirectUrl", "/planbee/groups/list");
  	        response.put("groupId", null);
  	    } else {
  	        response.put("redirectUrl", "/planbee/groups/" + groupId);
  	        response.put("groupId", groupId);
  	    }

  	    return ResponseEntity.ok(response);  // JSON 데이터 반환
  	}
  	
  	 // 모든 그룹 조회
    @GetMapping(value="/list", produces = "application/json; charset=utf-8")
    public List<GroupDTO> getAllGroups() {
        return gs.getAllGroups();
    }

    // 그룹 가입 (가입 후 바로 그룹 게시판으로 이동
    // http://localhost:8080/planbee/group/join?groupId={groupId}
    @PostMapping(value="/join", produces = "application/json; charset=utf-8")
    public ResponseEntity<Map<String, Object>> joinGroup(@RequestParam int groupId) {
        String userId = (String) se.getAttribute("sessionId");
        int success = gs.joinGroup(userId, groupId);

        Map<String, Object> response = new HashMap<>();

        if (success == 1) {
            response.put("redirectUrl", "/planbee/groups/" + groupId);
            response.put("groupId", groupId);
            response.put("message", "그룹 가입 완료");
        } else {
            response.put("redirectUrl", "/planbee/groups/list");
            response.put("groupId", null);
            response.put("message", "그룹 가입 실패");
        }

        return ResponseEntity.ok(response);
    }

}
