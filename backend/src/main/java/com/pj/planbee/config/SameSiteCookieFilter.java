package com.pj.planbee.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;     // ← 추가
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class SameSiteCookieFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필요 시 초기화 코드
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1) 요청-응답 체인 실행
        chain.doFilter(request, response);

        // 2) HTTP 요청/응답인지 확인
        if (response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
            HttpServletResponse res = (HttpServletResponse) response;
            HttpServletRequest req = (HttpServletRequest) request;  // ← 추가

            Collection<String> headers = res.getHeaders("Set-Cookie");
            if (headers == null || headers.isEmpty()) {
                return;
            }

            // 3) HTTPS 여부 판별
            boolean secure = req.isSecure();  // ← 변경: HTTP인지 HTTPS인지 체크

            List<String> updatedCookies = new ArrayList<>();
            for (String header : headers) {
                if (header != null && !header.toLowerCase().contains("samesite")) {
                    StringBuilder sb = new StringBuilder(header);
                    sb.append("; SameSite=None");  // 항상 SameSite=None 추가
                    if (secure) {
                        sb.append("; Secure");      // HTTPS 환경일 때만 Secure 추가
                    }
                    updatedCookies.add(sb.toString());
                } else {
                    updatedCookies.add(header);
                }
            }

            // 4) 쿠키 헤더 재설정
            res.setHeader("Set-Cookie", updatedCookies.get(0));
            for (int i = 1; i < updatedCookies.size(); i++) {
                res.addHeader("Set-Cookie", updatedCookies.get(i));
            }
        }
    }

    @Override
    public void destroy() {
        // 필요 시 정리 코드
    }
}
