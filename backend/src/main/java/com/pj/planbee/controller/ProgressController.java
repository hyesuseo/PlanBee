package com.pj.planbee.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pj.planbee.dto.BoardDTO;
import com.pj.planbee.dto.ProgressShareDTO;
import com.pj.planbee.dto.UserProgressDTO;
import com.pj.planbee.service.BoardService;
import com.pj.planbee.service.GroupService;
import com.pj.planbee.service.ProgressService;

@RestController
@RequestMapping("/groups")
public class ProgressController {

	// http://localhost:8080/planbee/groups/1/boards

	@Autowired
	ProgressService ps;
	@Autowired
	BoardService bs;
	@Autowired
	GroupService gs;
	@Autowired
	HttpSession se;

	// 일간 진척도 공유
	@PostMapping(value = "/{groupId}/boards/daily", produces = "application/json; charset=utf-8")
	public Map<String, Object> createDailyProgressPost(HttpSession session, @PathVariable int groupId,
	        @RequestParam(value = "date", required = false) String date) {
	    String sessionId = (String) session.getAttribute("sessionId");

	    if (date == null || date.isEmpty()) {
	        date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
	    }

	    ProgressShareDTO progressDTO = ps.getDailyProgress(sessionId, date);
	    String progressHtml = ps.generateProgressHtml(progressDTO);

	    BoardDTO dto = new BoardDTO();
	    dto.setUserId(sessionId);
	    dto.setGroupId(groupId);
	    dto.setPostTitle(sessionId + "님의 오늘 진척도");
	    dto.setPostContent(progressHtml);

	    int result = bs.writePost(dto);

	    Map<String, Object> response = new HashMap<>();

	    if (result == 1) {
	        int postId = bs.getLatestPostIdByUser(sessionId);
	        response.put("redirectUrl", "/planbee/groups/" + groupId + "/boards/" + postId);
	        response.put("postId", postId);
	        response.put("message", "오늘의 진척도 공유 성공");
	    } else {
	        response.put("message", "오늘의 진척도 공유 실패");
	    }

	    return response;
	}



	// 주간 진척도 공유
	@PostMapping(value = "/{groupId}/boards/weekly", produces = "application/json; charset=UTF-8")
	public Map<String, Object> createWeeklyProgressPost(HttpSession session, @PathVariable int groupId) {
	    String sessionId = (String) session.getAttribute("sessionId");

	    String progressHtml = ps.getWeeklyProgress(sessionId);

	    BoardDTO dto = new BoardDTO();
	    dto.setUserId(sessionId);
	    dto.setGroupId(groupId);
	    dto.setPostTitle(sessionId + "님의 주간 진척도");
	    dto.setPostContent(progressHtml);

	    int result = bs.writePost(dto);

	    Map<String, Object> response = new HashMap<>();

	    if (result == 1) {
	        int postId = bs.getLatestPostIdByUser(sessionId);
	        response.put("redirectUrl", "/planbee/groups/" + groupId + "/boards/" + postId);
	        response.put("postId", postId);
	        response.put("message", "주간 진척도 공유 성공");
	    } else {
	        response.put("message", "게시글 작성 실패");
	    }

	    return response;
	}



	// 그룹별 자동 정산
	@PostMapping(value = "/auto-post/{groupId}")
    public ResponseEntity<String> createAutoPost(@PathVariable int groupId) {
        String groupName = gs.getGroupName(groupId);
        String content = generateMonthlyRankingContent(groupId);

        BoardDTO dto = new BoardDTO();
        dto.setPostTitle(groupName + " 그룹 월간 순위");
        dto.setPostContent(content);
        dto.setUserId(groupName);
        dto.setGroupId(groupId);

        bs.writePost(dto);
        return ResponseEntity.ok(groupName + "upload success");
    }

    public String generateMonthlyRankingContent(int groupId) {
        LocalDate now = LocalDate.now();
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());
        String lastMonth = lastMonthStart.format(DateTimeFormatter.ofPattern("yyMM"));

        List<UserProgressDTO> ranking = ps.getGroupMonthlyProgressRanking(groupId, lastMonth);
        List<Map<String, Object>> maxStreaks = ps.getGroupMaxStreaks(groupId);
        List<Map<String, Object>> curStreaks = ps.getGroupCurrentStreaks(groupId);

        String startDateStr = lastMonthStart.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String endDateStr = lastMonthEnd.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String groupName = gs.getGroupName(groupId);

        StringBuilder content = new StringBuilder();
        content.append("<div style='width: 400px; background: #121212; border-radius: 15px; padding: 20px; color: white; font-family: Arial, sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center;'>");
        content.append("<h2 style='margin: 0; font-size: 22px; text-align: center;'><b>").append(groupName).append("</b>의 월간 순위</h2>");
        content.append("<h3 style='margin: 10px 0; font-size: 16px; font-weight: lighter; text-align: center;'>")
                .append(startDateStr).append(" ~ ").append(endDateStr).append("</h3>");

        content.append("<h3 style='margin: 15px 0; font-size: 18px; font-weight: bold; text-align: center;'>월간 평균 진척도 순위</h3>");
        for (int i = 0; i < ranking.size(); i++) {
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "📉";
            content.append("<div style='width: 100%; display: flex; justify-content: space-between; padding: 5px 10px;'>")
                    .append("<span style='color: white; font-size: 14px;'>")
                    .append(medal).append(" ").append(ranking.get(i).getUserId())
                    .append("</span><span style='color: white; font-size: 14px;'>")
                    .append(ranking.get(i).getProgress()).append("%</span></div>");
        }

        content.append("<h3 style='margin: 15px 0; font-size: 18px; font-weight: bold; text-align: center;'>역대 최대 스트릭 순위</h3>");
        for (int i = 0; i < Math.min(3, maxStreaks.size()); i++) {
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "";
            content.append("<div style='width: 100%; display: flex; justify-content: space-between; padding: 5px 10px;'>")
                    .append("<span style='color: white; font-size: 14px;'>")
                    .append(medal).append(" ").append(maxStreaks.get(i).get("user_id"))
                    .append("</span><span style='color: white; font-size: 14px;'>")
                    .append(maxStreaks.get(i).get("streakDays")).append("일</span></div>");
        }

        content.append("<h3 style='margin: 15px 0; font-size: 18px; font-weight: bold; text-align: center;'>현재 최대 스트릭 순위</h3>");
        for (int i = 0; i < Math.min(3, curStreaks.size()); i++) {
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "";
            content.append("<div style='width: 100%; display: flex; justify-content: space-between; padding: 5px 10px;'>")
                    .append("<span style='color: white; font-size: 14px;'>")
                    .append(medal).append(" ").append(curStreaks.get(i).get("user_id"))
                    .append("</span><span style='color: white; font-size: 14px;'>")
                    .append(curStreaks.get(i).get("streakDays")).append("일</span></div>");
        }

        content.append("</div>");
        return content.toString();
    }

}
