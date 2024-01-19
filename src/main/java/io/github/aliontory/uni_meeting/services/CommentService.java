package io.github.aliontory.uni_meeting.services;

import io.github.aliontory.uni_meeting.beans.vo.CommentVO;

import java.util.List;

public interface CommentService {
    List<List<CommentVO>> getListByBno(Long bno, Long boardMid);


    Long insert(CommentVO commentVO, Long boardMid);

    int delete(Long cno);

    int update(long cno, String commentContent);
}
