package com.pj.planbee.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pj.planbee.controller.BoardController;
import com.pj.planbee.controller.ProgressController;
import com.pj.planbee.dto.BoardDTO;
import com.pj.planbee.dto.GroupDTO;
import com.pj.planbee.dto.UserProgressDTO;
import com.pj.planbee.service.GroupService;
import com.pj.planbee.service.ProgressService;

@Component
public class AutoMonthlyProgressConfig {

    @Autowired ProgressController pc;

    @Autowired  GroupService groupService;
    @Autowired ProgressService ps;

    /**
     * 매월 마지막 날 00:00에 실행되어 각 그룹별 월간 순위 게시글을 자동으로 작성
     */
    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 자정 실행
    public void generateMonthlyGroupRankingPosts() {
        try {
            List<GroupDTO> allGroups = groupService.getAllGroups(); // 그룹 리스트 가져오기

            for (GroupDTO group : allGroups) {    
               String content = pc.generateMonthlyRankingContent(group.getGroupId()); // 게시글 내용 생성

                BoardDTO dto = new BoardDTO();
                dto.setPostTitle(group.getGroupName() + " 그룹 월간 순위");
                dto.setPostContent(content);
                dto.setUserId(group.getGroupName()); // 작성자는 그룹명으로 설정
                dto.setGroupId(group.getGroupId());

                pc.createAutoPost(group.getGroupId()); // BoardDTO를 직접 전달
            }

            System.out.println("모든 그룹의 월간 순위 게시글이 자동으로 등록되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

