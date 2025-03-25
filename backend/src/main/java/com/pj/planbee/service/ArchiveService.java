package com.pj.planbee.service;

import java.util.List;

import com.pj.planbee.dto.ArchiveDTO;

public interface ArchiveService {

	List<ArchiveDTO> getPagedArchives(String userId, int offset, int limit);
    List<ArchiveDTO> searchArchivesByDate(String userId, String date);
    List<ArchiveDTO> searchByDetail(String userId, String searchType);	
    List<ArchiveDTO> searchArchives(String userId, String searchType, String query);
}