package com.pj.planbee.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import com.pj.planbee.dto.ArchiveDTO;

@Component
public class CacheConfig {

    public static final int CACHE_SIZE = 30;

    // 동시성 해결
    private final Map<String, List<ArchiveDTO>> archiveCache = new ConcurrentHashMap<>();

    // 멀티스레드 환경에서 동기화 보장
    private final ReentrantLock cacheLock = new ReentrantLock();

    // 캐시 추가 (static 제거)
    public void putCache(String cacheKey, List<ArchiveDTO> value) {
        cacheLock.lock();
        try {
            if (archiveCache.size() >= CACHE_SIZE) {
                String eldestKey = archiveCache.keySet().iterator().next();
                archiveCache.remove(eldestKey);
                System.out.println("캐시 삭제: " + eldestKey);
            }
            archiveCache.put(cacheKey, value);
            System.out.println("캐시 추가: " + cacheKey);
        } finally {
            cacheLock.unlock();
        }
    }

    // 캐시 조회 (static 제거)
    public List<ArchiveDTO> getCache(String cacheKey) {
        return archiveCache.get(cacheKey);
    }

    // 캐시 상태 출력
    public void printCacheStatus() {
        cacheLock.lock();
        try {
            System.out.println("현재 캐시된 데이터 목록:");
            archiveCache.forEach((key, value) -> 
                System.out.println(" - " + key + " 의 데이터 : " + value.size() + " 개")
            );
        } finally {
            cacheLock.unlock();
        }
    }
}
