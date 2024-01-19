package io.github.aliontory.uni_meeting.controllers;

import io.github.aliontory.uni_meeting.beans.vo.BoardVO;
import io.github.aliontory.uni_meeting.beans.vo.Criteria;
import io.github.aliontory.uni_meeting.beans.vo.PageDTO;
import io.github.aliontory.uni_meeting.beans.vo.PlaceVO;
import io.github.aliontory.uni_meeting.repository.MemberRepository;
import io.github.aliontory.uni_meeting.services.BoardService;
import io.github.aliontory.uni_meeting.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/{schoolName}/board/{boardId}/*")
@Log4j2
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole(#schoolName.toUpperCase())")
public class BoardController {
    private final BoardService service;
    private final CommentService commentService;
    private final MemberRepository memberRepository;

    @GetMapping("list")
    //@PreAuthorize("hasRole('ADMIN') or hasRole(#cri.schoolName.toUpperCase())")
    public String list(@PathVariable String schoolName, @PathVariable int boardId, Criteria cri, Model model) {
        log.info("------------------------list--------------------");

        //주소창에 ?schoolName=snu 를 쓰는 식으로 cri의 schoolName값을 변조할 수 있음.
        //따라서 권한 체크에 사용된 PathVariable schoolName과 동기화 해야 함.
        cri.setSchoolName(schoolName);
        cri.setBoardId(boardId);

        model.addAttribute("schoolName", cri.getSchoolName());
        model.addAttribute("boardId", cri.getBoardId());
        model.addAttribute("boardTitle", BoardVO.getBoardTitle(cri.getBoardId()));
        model.addAttribute("pageMaker", new PageDTO(cri, service.getTotal(cri)));
        model.addAttribute("list", service.getList(cri));

        return "board/list";
    }

    //@PreAuthorize("hasRole(#schoolName.toUpperCase())")
    @GetMapping("{bno}/get")
    //@PreAuthorize("hasRole('ADMIN') or hasRole(#schoolName.toUpperCase())")
    public String get(@PathVariable String schoolName, @PathVariable int boardId,
                      @PathVariable Long bno, Model model, String old) {
        log.info("------------------------get--------------------");
        model.addAttribute("boardTitle", BoardVO.getBoardTitle(boardId));

        BoardVO boardVO = service.get(bno,schoolName,boardId);
        model.addAttribute("board", boardVO);

        return "/board/get";
    }


    @GetMapping("register")
    public String register(@PathVariable String schoolName, @PathVariable int boardId, Model model) {
        model.addAttribute("boardTitle", BoardVO.getBoardTitle(boardId));

        return "/board/register";
    }

    @PostMapping("register")
    public String register(@PathVariable String schoolName, @PathVariable int boardId, BoardVO boardVO) {
        boardVO.setSchoolName(schoolName);
        boardVO.setBoardId(boardId);

        service.register(boardVO);

        return "redirect:/" + boardVO.getSchoolName() + "/board/" + boardVO.getBoardId() + "/list";
    }

    @PostMapping("{bno}/remove")
    public String remove(@PathVariable String schoolName, @PathVariable int boardId, @PathVariable Long bno) {
        log.info("-----------------------remove : " + bno + " in " + schoolName + "/board/" + boardId);
        service.remove(bno, schoolName, boardId);

        return "redirect:/" + schoolName + "/board/" + boardId + "/list";
    }

    @GetMapping("{bno}/modify")
    public String modify(@PathVariable String schoolName, @PathVariable int boardId, @PathVariable Long bno, Model model) {
        BoardVO boardVO = service.get(bno, schoolName, boardId);
        model.addAttribute("boardVO", boardVO);
        model.addAttribute("boardTitle", BoardVO.getBoardTitle(boardVO.getBoardId()));

        return "/board/modify";
    }

    @PostMapping("{bno}/modify")
    public String modify(@PathVariable String schoolName, @PathVariable int boardId, BoardVO boardVO) {
        boardVO.setSchoolName(schoolName);
        boardVO.setBoardId(boardId);

        service.modify(boardVO);
        return "redirect:/" + boardVO.getSchoolName() + "/board/" + boardVO.getBoardId() + "/list";
    }
}
