package com.pj.planbee.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.planbee.config.CacheConfig;
import com.pj.planbee.dto.ArchiveDTO;
import com.pj.planbee.mapper.ArchiveMapper;

@Service
public class ArchiveServiceImpl implements ArchiveService {

    @Autowired ArchiveMapper mapper;
    @Autowired CacheConfig cacheConfig;  // Bean으로 주입

    @Override
    public List<ArchiveDTO> getPagedArchives(String userId, int offset, int limit) {
        String cacheKey = userId + "_page_" + offset;  // 캐시 키 (페이지 기준)
        List<ArchiveDTO> cachedData = cacheConfig.getCache(cacheKey);  // 주입받은 객체 사용

        if (cachedData != null) {
            System.out.println("캐시에서 데이터 반환: " + cacheKey);
            return cachedData;
        }

        // 캐시에 없으면 DB에서 가져오기
        List<ArchiveDTO> archives = mapper.getPagedArchives(userId, offset, limit);

        // 캐싱 후 반환
        if (!archives.isEmpty()) {
            cacheConfig.putCache(cacheKey, archives);  // ✅ 주입받은 객체 사용
        }
        return archives;
    }

    @Override
    public List<ArchiveDTO> searchArchivesByDate(String userId, String date) {
        return mapper.findArchivesByDate(userId, date);
    }

    @Override
    public List<ArchiveDTO> searchByDetail(String userId, String keyword) {
        return mapper.searchByDetail(userId, keyword);
    }
    
    @Override
    public List<ArchiveDTO> searchArchives(String userId, String searchType, String query) {
        if ("date".equalsIgnoreCase(searchType)) {
            return searchArchivesByDate(userId, query);
        } else if ("content".equalsIgnoreCase(searchType)) {
            return searchByDetail(userId, query);
        } else {
            throw new IllegalArgumentException("유효하지 않은 searchType 입니다. 'date' 또는 'content'를 사용하세요.");
        }
    }
}