package com.pj.planbee.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import com.pj.planbee.dto.ArchiveDTO;

@Component
public class CacheConfig {
	
	//Archive 페이지 cache
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
    
    //todoList의 todoId에 대한 캐시
    private final Map<String, Integer> todoIdCache = new ConcurrentHashMap<String, Integer>();
    private final ReentrantLock todoIdCacheLock = new ReentrantLock();
    
    public void putTodoIdCache(String sessionId, String tdDate, int todoId) {
    	String key = sessionId + "_" + tdDate; //캐시 키 생성
    	todoIdCacheLock.lock(); // 동기화를 위한 lock, 나만 접근할 수 있도록 문 잠그는 것
    	try {
			todoIdCache.put(key, todoId); //캐시에 저장
			System.out.println(key +"캐시 추가");
		} finally {
			todoIdCacheLock.unlock(); //작업 완료되었으면 해제, unlock안하면 다른스레드는 영원히 들어갈 수 없음
		}
    }
    //캐시에서 값을 가져오는 메서드 	
    public Integer getTodoIdCache(String sessionId, String tdDate) {
    	    String key = sessionId + "_" + tdDate;
    	    return todoIdCache.get(key);
    	}
    
    //캐시 전체를 초기화하는 메서드 
    public void clearTodoIdCache() {
    	   todoIdCacheLock.lock(); //캐시 전체 접근이므로 lock 사용
    	    try {
    	        todoIdCache.clear();
    	        System.out.println("todoId 캐시 초기화 완료!");
    	    } finally {
    	        todoIdCacheLock.unlock();
    	    }
    	}  
    
}
