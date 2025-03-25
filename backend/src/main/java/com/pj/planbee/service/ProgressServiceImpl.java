package com.pj.planbee.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.planbee.dto.ProgressShareDTO;
import com.pj.planbee.dto.UserProgressDTO;
import com.pj.planbee.mapper.ProgressMapper;

@Service
public class ProgressServiceImpl implements ProgressService {
	
    @Autowired ProgressMapper pm;

    @Override
    public ProgressShareDTO getDailyProgress(String userId, String date) {
        int total = pm.getTotalTasks(userId, date);
        int completed = pm.getCompletedTasks(userId, date);
        return new ProgressShareDTO(userId, date, completed, total);
    }

    @Override
    public String generateProgressHtml(ProgressShareDTO progressDTO) {
        int percentage = (progressDTO.getTotal() > 0) ? 
            (int) ((progressDTO.getCompleted() / (double) progressDTO.getTotal()) * 100) : 0;

        String barColor;
        if (percentage <= 30) {
            barColor = "#F35C41"; // 빨강
        } else if (percentage > 30 && percentage <= 70) {
            barColor = "#F4CC3A"; // 노랑
        } else {
            barColor = "#2BB51F"; // 초록
        }

        // 날짜 형식 변환 (YYYY-MM-DD)
        String formattedDate = "20" + progressDTO.getDate().substring(0, 2) + "-" +
                               progressDTO.getDate().substring(2, 4) + "-" +
                               progressDTO.getDate().substring(4, 6);

        return "<div style='width: 400px; background: #121212; border-radius: 15px; padding: 20px; color: white; font-family: Arial, sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center;'><h2 style='margin: 0; font-size: 25px; text-align: center; font-weight: bolder;'>" 
        + progressDTO.getUserId() + "님의 오늘 진척도</h2><h3 style='margin: 10px 0; font-size: 15px; font-weight: bold; text-align: center;'>" + formattedDate 
        + "</h3><p style='margin: 5px 0; font-size: 14px; color: #CCCCCC; text-align: center;'>오늘의 진척도</p><p style='margin: 5px 0; font-size: 16px; text-align: center;'>" 
        + progressDTO.getCompleted() + "개 / " + progressDTO.getTotal() + "개</p><div style='width: 100%; background: #ffffff; border-radius: 10px; padding: 5px; margin-top: 10px; position: relative;'>"
        + "<div style='width: " + percentage + "%; background: " + barColor + "; border-radius: 15px; height: 30px;'></div><div style='position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); color: black; font-weight: bold; font-size: 16px;'>" 
        + percentage + "%</div></div></div>";

    }
    
    @Override
    public String getWeeklyProgress(String userId) {
        // 오늘 날짜 계산 (YYMMDD 형식)
        LocalDate today = LocalDate.now();
        String endDate = today.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String startDate = today.minusDays(6).format(DateTimeFormatter.ofPattern("yyMMdd"));

        // DB에서 7일간의 데이터 가져오기
        List<Map<String, Object>> weeklyData = pm.getWeeklyProgress(userId, startDate, endDate);

        // 날짜 리스트 및 퍼센트 리스트 생성
        List<String> dates = new ArrayList<>();
        List<Integer> percentages = new ArrayList<>();

        // 주어진 날짜 범위 내에서 데이터를 채우기 위해 기본값을 설정
        for (int i = 0; i < 7; i++) {
            String date = today.minusDays(6 - i).format(DateTimeFormatter.ofPattern("MM-dd")); // ✅ 변경된 부분 (MM-DD 형식)
            dates.add(date);
            percentages.add(0); // 기본값: 0%
        }

        // DB에서 가져온 데이터 반영
        for (Map<String, Object> data : weeklyData) {
            String date = data.get("todo_date").toString().substring(2, 4) + "-" + data.get("todo_date").toString().substring(4, 6); // ✅ YYMMDD → MM-DD 변환
            int total = Integer.parseInt(data.get("total_tasks").toString());
            int completed = Integer.parseInt(data.get("completed_tasks").toString());
            int percentage = (total > 0) ? (int) ((completed / (double) total) * 100) : 0;

            // 날짜 리스트에서 해당 날짜의 인덱스 찾아서 퍼센트 업데이트
            int index = dates.indexOf(date);
            if (index != -1) {
                percentages.set(index, percentage);
            }
        }

        return generateWeeklyProgressHTML(userId, dates, percentages);
    }


    @Override
    public String generateWeeklyProgressHTML(String user, List<String> dates, List<Integer> percentages) {
        StringBuilder progressBars = new StringBuilder();

        // 7일간 데이터 생성 (각 날짜별로 바 추가)
        for (int i = 0; i < dates.size(); i++) {
            int percentage = percentages.get(i);
            String barColor = getBarColor(percentage);

            progressBars.append(
                "<div style='width: 100%; display: flex; align-items: center; gap: 10px;'>"
                    + "<span style='width: 70px; text-align: center; color: white; font-size: 14px;'>" + dates.get(i) + "</span>"
                    + "<div style='width: 100%; background: white; border-radius: 10px; padding: 5px; position: relative;'>"
                    + "<div style='width: " + percentage + "%; background: " + barColor + "; border-radius: 15px; height: 25px;'></div>"
                    + "<span style='position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);"
                    + "color: black; font-size: 14px; font-weight: bold;'>" + percentage + "%</span>"
                    + "</div></div>"
            );
        }

        return "<div style='width: 600px; background: #121212; border-radius: 15px; padding: 20px; color: white; "
                + "font-family: Arial, sans-serif; display: flex; flex-direction: column; align-items: center; justify-content: center;'>"
                + "<h2 style='margin: 0; font-size: 25px; text-align: center; font-weight: bolder;'><b>" + user + "</b>님의 주간 진척도</h2>"
                + "<h3 style='margin: 10px 0; font-size: 15px; font-weight: lighter; text-align: center; margin-top: 20px;'>" 
                + dates.get(0) + " ~ " + dates.get(6) + "</h3>"
                + "<div style='width: 100%; display: flex; flex-direction: column; gap: 10px; margin-top: 20px'>"
                + progressBars.toString()
                + "</div></div>";
    }

    private String getBarColor(int percentage) {
        if (percentage <= 30) return "#F35C41";  // 빨강
        if (percentage <= 70) return "#F4CC3A";  // 노랑
        return "#2BB51F";  // 초록
    }

    @Override
    public List<UserProgressDTO> getGroupMonthlyProgressRanking(int groupId, String yyMM) {
        return pm.getGroupMonthlyProgressRanking(groupId, yyMM);
    }

    @Override
    public List<Map<String, Object>> getGroupMaxStreaks(int groupId) {
        return pm.getGroupMaxStreaks(groupId);
    }

    @Override
    public List<Map<String, Object>> getGroupCurrentStreaks(int groupId) {
        return pm.getGroupCurrentStreaks(groupId);
    }
}