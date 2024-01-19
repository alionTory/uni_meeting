package io.github.aliontory.uni_meeting.services;

import io.github.aliontory.uni_meeting.beans.vo.BoardVO;
import io.github.aliontory.uni_meeting.beans.vo.Criteria;
import io.github.aliontory.uni_meeting.beans.vo.PlaceVO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface BoardService {
    int getTotal(Criteria cri);

    void register(BoardVO boardVO);

    List<BoardVO> getList(Criteria cri);

    BoardVO get(Long bno, String schoolName, int boardId);

    BoardVO getByBno(Long bno);

    boolean remove(Long bno, String schoolName, int boardId);

    boolean modify(BoardVO boardVO);

    PlaceVO getPlaceFromKakao(long placeId);
}
