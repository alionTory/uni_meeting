package io.github.aliontory.uni_meeting.dao;

import io.github.aliontory.uni_meeting.beans.vo.BoardVO;
import io.github.aliontory.uni_meeting.beans.vo.Criteria;
import io.github.aliontory.uni_meeting.domain.Member;
import io.github.aliontory.uni_meeting.mapper.BoardMapper;
import io.github.aliontory.uni_meeting.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BoardDAO {
    private final BoardMapper boardMapper;
    private final MemberRepository memberRepository;

    public int getTotal(Criteria cri) {
        return boardMapper.getTotal(cri);
    }

    public void register(BoardVO boardVO) {
        boardMapper.insert(boardVO);
    }

    public List<BoardVO> getList(Criteria cri) {
        cri.setParam();
        List<BoardVO> boardVOS = boardMapper.getList(cri);
        boardVOS.forEach(this::setBoardVOWriterName);
        return boardVOS;
    }

    public BoardVO get(Long bno, String schoolName, int boardId) {
        BoardVO boardVO = boardMapper.read(bno, schoolName, boardId);
        setBoardVOWriterName(boardVO);
        return boardVO;
    }

    public BoardVO getByBno(Long bno){
        BoardVO boardVO = boardMapper.readByBno(bno);
        setBoardVOWriterName(boardVO);
        return boardVO;
    }

    private void setBoardVOWriterName(BoardVO boardVO) {
        Optional<Member> memberOptional = memberRepository.findById(boardVO.getMid());
        if (memberOptional.isPresent()) {
            String writerName = memberOptional.get().getName();
            boardVO.setWriterName(writerName);
        }
    }

    public boolean remove(Long bno, String schoolName, int boardId) {
        return boardMapper.delete(bno, schoolName, boardId) != 0;
    }

    public boolean modify(BoardVO vo) {
        return boardMapper.update(vo) != 0;
    }
}
