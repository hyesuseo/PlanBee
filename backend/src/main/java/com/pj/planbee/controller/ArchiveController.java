package com.pj.planbee.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pj.planbee.dto.ArchiveDTO;
import com.pj.planbee.service.ArchiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "Archive API", description = "아카이브 데이터 관련 API")
@RestController
@RequestMapping("/archive")
public class ArchiveController {

    @Autowired 
    ArchiveService as;
    
    @ApiOperation(value = "페이징 기반 아카이브 조회", 
                  notes = "페이지 번호와 한 페이지에 표시할 항목 수를 이용하여 아카이브 데이터를 조회합니다.")
    @GetMapping(produces = "application/json; charset=utf-8")
    public List<ArchiveDTO> getPagedArchives(
            @ApiParam(value = "페이지 번호 (default: 0)", defaultValue = "0") 
            @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "한 페이지에 표시할 항목 수 (default: 6)", defaultValue = "6") 
            @RequestParam(defaultValue = "6") int limit,
            @ApiIgnore HttpSession session) {

        String userId = (String) session.getAttribute("sessionId");
        int offset = page * limit;
        return as.getPagedArchives(userId, offset, limit);
    }
    
    @ApiOperation(value = "아카이브 검색", 
                  notes = "searchType과 query 파라미터를 이용하여 아카이브 데이터를 검색합니다. "
                        + "searchType은 'date' 또는 'content' 값을 사용합니다.")
    @GetMapping(value = "/search", produces = "application/json; charset=utf-8")
    public List<ArchiveDTO> searchArchives(
            @ApiParam(value = "검색 타입: 'date' 또는 'content'", required = false) 
            @RequestParam(name = "searchType", required = false) String searchType,
            @ApiParam(value = "검색 쿼리", required = false) 
            @RequestParam(name = "query", required = false) String query,
            @ApiIgnore HttpSession session) {

        String userId = (String) session.getAttribute("sessionId");
      
        if ("date".equalsIgnoreCase(searchType)) {
            return as.searchArchivesByDate(userId, query);
        } else if ("content".equalsIgnoreCase(searchType)) {
            return as.searchByDetail(userId, query);
        } else {
            throw new IllegalArgumentException("유효하지 않은 searchType 입니다. 'date' 또는 'content'를 사용하세요.");
        }
    }
}
