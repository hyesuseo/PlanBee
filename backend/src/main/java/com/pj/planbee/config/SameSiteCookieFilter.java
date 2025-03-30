package com.pj.planbee.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SameSiteCookieFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 필요 시 초기화 코드
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        chain.doFilter(request, response);

        if (response instanceof HttpServletResponse) {
            HttpServletResponse res = (HttpServletResponse) response;
            Collection<String> headers = res.getHeaders("Set-Cookie");

            if (headers != null && !headers.isEmpty()) {
                List<String> updatedCookies = new ArrayList<>();

                for (String header : headers) {
                    if (header != null && !header.toLowerCase().contains("samesite")) {
                        updatedCookies.add(header + "; SameSite=None; Secure");
                    } else {
                        updatedCookies.add(header); // 이미 설정된 경우
                    }
                }

                // 업데이트된 쿠키가 있을 때만 헤더 설정
                if (!updatedCookies.isEmpty()) {
                    res.setHeader("Set-Cookie", updatedCookies.get(0)); // 첫 번째는 setHeader
                    for (int i = 1; i < updatedCookies.size(); i++) {
                        res.addHeader("Set-Cookie", updatedCookies.get(i)); // 나머진 addHeader
                    }
                }
            }
        }
    }

    @Override
    public void destroy() {
        // 필요 시 정리 코드
    }
}

