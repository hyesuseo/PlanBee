package com.pj.planbee.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.pj.planbee.dto.ArchiveDTO;

@Mapper
public interface ArchiveMapper {
    
	 List<ArchiveDTO> getPagedArchives(@Param("userId") String userId, @Param("offset") int offset, @Param("limit") int limit);
	 List<ArchiveDTO> findArchivesByDate(@Param("userId") String userId, @Param("date") String date);
	 List<ArchiveDTO> searchByDetail(@Param("userId") String userId, @Param("keyword") String keyword);
	 
}