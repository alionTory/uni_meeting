package io.github.aliontory.uni_meeting.mapper;

import io.github.aliontory.uni_meeting.beans.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<CommentVO> getListByBno(Long bno);
    Long insert(CommentVO commentVO);
    CommentVO read(Long cno);
    List<CommentVO> readReplies(Long cno);
    int delete(Long cno);
    int deleteByBno(Long bno);
    int update(CommentVO commentVO);
}
