package io.github.aliontory.uni_meeting.mapper;

import io.github.aliontory.uni_meeting.beans.vo.BoardVO;
import io.github.aliontory.uni_meeting.beans.vo.Criteria;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface BoardMapper {
    public int getTotal(Criteria cri);

    public List<BoardVO> getList(Criteria cri);

    public void insert(BoardVO boardVO);

    public BoardVO read(Long bno, String schoolName, int boardId);
    public BoardVO readByBno(Long bno);

    public int delete(Long bno, String schoolName, int boardId);

    public int update(BoardVO boardVO);
}
