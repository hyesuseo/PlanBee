package com.pj.planbee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.pj.planbee.service.TodoListService;

@Configuration
@EnableScheduling
public class AutoSaveConfig {

	private final TodoListService ts; // 생성자 주입방식으로 변경함

	// 생성자 주입 방식으로 객체 주입
	public AutoSaveConfig(TodoListService ts) {
		this.ts = ts;
	}

	@Scheduled(cron = "59 59 23 * * *") // 매일 자정에 실행
	public void scheduledSaveArchive() {

		int result = ts.saveArchive();
		System.out.println("아카이브 자동 백업 실행 결과: " + result);
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
	public void saveDetailArchive() {
		int result = ts.saveArchiveDetail();
		System.out.println("아카이브 디테일 자동백업 실행결과: " + result);
	}
}
