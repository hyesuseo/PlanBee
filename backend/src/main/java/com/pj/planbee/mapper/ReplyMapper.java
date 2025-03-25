package com.pj.planbee.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.pj.planbee.dto.ReplyDTO;

@Mapper
public interface ReplyMapper {
    int insertReply(ReplyDTO reply);   // 댓글 등록
    List<ReplyDTO> getRepliesByPostId(int postId);  // 특정 게시글의 Id로 댓글 목록 조회
    int updateReply(ReplyDTO reply);  // 댓글 수정
    int deleteReply(@Param("replyId") int replyId, @Param("postId") int postId, @Param("userId") String userId); // postId & userId 추가
    List<ReplyDTO> getReplyAndRepReplyByPostId(int postId);
}