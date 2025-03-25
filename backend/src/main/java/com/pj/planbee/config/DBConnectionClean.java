package com.pj.planbee.config;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

@Component
public class DBConnectionClean {

   @PreDestroy
   public void cleanup() {
       System.out.println("▶ JDBC cleanup: AbandonedConnectionCleanupThread 종료 시도");
       AbandonedConnectionCleanupThread.checkedShutdown();
   }

}
