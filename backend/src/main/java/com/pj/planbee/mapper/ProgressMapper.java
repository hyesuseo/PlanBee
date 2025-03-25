package com.pj.planbee.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pj.planbee.dto.UserProgressDTO;

@Mapper
public interface ProgressMapper {

	int getTotalTasks(@Param("userId") String userId, @Param("date") String date);

	int getCompletedTasks(@Param("userId") String userId, @Param("date") String date);

	List<Map<String, Object>> getWeeklyProgress(@Param("userId") String userId, @Param("startDate") String startDate,
			@Param("endDate") String endDate);

	// 그룹별 월간 평균 진척도 조회
	List<UserProgressDTO> getGroupMonthlyProgressRanking(@Param("groupId") int groupId, @Param("yyMM") String yyMM);

	// 그룹 내 최대 스트릭 조회
	List<Map<String, Object>> getGroupMaxStreaks(@Param("groupId") int groupId);

	// 그룹 내 현재 스트릭 조회
	List<Map<String, Object>> getGroupCurrentStreaks(@Param("groupId") int groupId);
}
