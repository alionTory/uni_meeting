package io.github.aliontory.uni_meeting.dao;

import io.github.aliontory.uni_meeting.beans.vo.CommentVO;
import io.github.aliontory.uni_meeting.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentDAO {
    private final CommentMapper commentMapper;

    public List<CommentVO> getListByBno(Long bno) {
        return commentMapper.getListByBno(bno);
    }

    public Long insert(CommentVO commentVO) {
        return commentMapper.insert(commentVO);
    }

    public CommentVO get(Long cno) {
        return commentMapper.read(cno);
    }
    public List<CommentVO> getReplies(Long cno){
        return commentMapper.readReplies(cno);
    }

    public int delete(Long cno) {
        return commentMapper.delete(cno);
    }

    public int deleteByBno(Long bno) {
        return commentMapper.deleteByBno(bno);
    }

    public int update(CommentVO commentVO) {
        return commentMapper.update(commentVO);
    }
}
