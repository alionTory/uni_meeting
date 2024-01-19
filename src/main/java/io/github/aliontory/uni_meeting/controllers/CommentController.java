package io.github.aliontory.uni_meeting.controllers;

import io.github.aliontory.uni_meeting.beans.vo.BoardVO;
import io.github.aliontory.uni_meeting.beans.vo.CommentVO;
import io.github.aliontory.uni_meeting.security.MemberSecurityDTO;
import io.github.aliontory.uni_meeting.services.BoardService;
import io.github.aliontory.uni_meeting.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

/*
preAuthorize를 통해 인증된 사용자만 접근하도록 해야 함.
그렇지 않으면 나중에 댓글 처리 과정에서 principal객체가 MemberSecurityDTO로
변환되지 않아 500오류 발생
 */
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/comments")
@PreAuthorize("isAuthenticated()")
public class CommentController {
    private final CommentService commentService;
    private final BoardService boardService;

    private BoardVO checkSchoolAuthority(Long bno) {
        BoardVO boardVO = boardService.getByBno(bno);
        String schoolName = boardVO.getSchoolName();

        // 어드민 권한 또는 학교 권한이 있어야 함
        MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentMember.hasAuthorities("ADMIN") && !currentMember.hasAuthorities(schoolName.toUpperCase())) {
            throw new AccessDeniedException("403");
        }

        // db에 저장된 게시글 정보 리턴
        return boardVO;
    }

    @GetMapping("/list/{bno}")
    public List<List<CommentVO>> getList(@PathVariable Long bno) {
        BoardVO boardVO = checkSchoolAuthority(bno);

        log.info("CommentController getList");
        return commentService.getListByBno(bno, boardVO.getMid());
    }

    // 브라우저에서 JSON 타입으로 데이터를 전송하고
    // 서버에서는 댓글 처리 결과에 따라 문자열로 결과를 리턴
    // consume : Ajax를 통해 전달받은 데이터의 타입
    // produces : Ajax의 success:function(result)의 result에 전달할 데이터의 타입
    // @RequestBody : JSON 문자열을 CommentVO로 변환
    // ResponseEntity : 서버의 상태코드, 응답 메시지 등을 받을 수 있는 타입
    @PostMapping(value = "/new", consumes = "application/json", produces = "text/plain; charset=utf-8")
    public ResponseEntity<String> insert(@RequestBody CommentVO commentVO)
            throws UnsupportedEncodingException {
        log.info("댓글 등록");
        log.info(commentVO);
        Long boardMid = checkSchoolAuthority(commentVO.getBno()).getMid();
        Long insertCount = commentService.insert(commentVO, boardMid);

        if (insertCount != 0) {
            return new ResponseEntity<>(new String("댓글이 등록되었습니다.".getBytes(), "UTF-8"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping(value = "/{cno}", consumes = "application/json", produces = "text/plain; charset=utf-8")
    public ResponseEntity<String> modify(@RequestBody String commentContent, @PathVariable Long cno)
            throws UnsupportedEncodingException {
        int updateCount = commentService.update(cno, commentContent);

        if (updateCount == 1)
            return new ResponseEntity<>(new String("댓글이 수정되었습니다.".getBytes(), "UTF-8"), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping(value = "/{cno}", produces = "text/plain; charset=utf-8")
    public String remove(@PathVariable Long cno) {
        return commentService.delete(cno) == 1 ? "댓글이 삭제되었습니다." : "댓글 삭제 실패.";
    }
}
