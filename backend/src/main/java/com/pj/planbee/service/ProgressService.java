package com.pj.planbee.service;

import java.util.List;
import java.util.Map;

import com.pj.planbee.dto.ProgressShareDTO;
import com.pj.planbee.dto.UserProgressDTO;

public interface ProgressService {

	ProgressShareDTO getDailyProgress(String userId, String date);

	String generateProgressHtml(ProgressShareDTO progressDTO);

	String getWeeklyProgress(String userId);

	String generateWeeklyProgressHTML(String user, List<String> dates, List<Integer> percentages);

	// 그룹별 월간 평균 진척도 조회
	List<UserProgressDTO> getGroupMonthlyProgressRanking(int groupId, String yyMM);

	// 그룹 내 최대 스트릭 조회
	List<Map<String, Object>> getGroupMaxStreaks(int groupId);

	// 그룹 내 현재 스트릭 조회
	List<Map<String, Object>> getGroupCurrentStreaks(int groupId);
}
