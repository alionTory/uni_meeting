package io.github.aliontory.uni_meeting.controllers;

import io.github.aliontory.uni_meeting.beans.vo.BoardVO;
import io.github.aliontory.uni_meeting.beans.vo.Criteria;
import io.github.aliontory.uni_meeting.security.MemberSecurityDTO;
import io.github.aliontory.uni_meeting.services.BoardService;
import io.github.aliontory.uni_meeting.services.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class MainController {
    private final BoardService boardService;
    private final SchoolService schoolService;

    @GetMapping("{schoolName}/main")
    @PreAuthorize("hasRole('ADMIN') or hasRole(#schoolName.toUpperCase())")
    public String main(@PathVariable String schoolName, Model model) {
        log.info("-----------------------------main------------------------------");

        String schoolNameKor = schoolService.get(schoolName).getSchoolNameKor();
        model.addAttribute("schoolNameKor", schoolNameKor);

        Criteria hobby = new Criteria(schoolName, 1, 1, 6);
        List<BoardVO> listHobby = boardService.getList(hobby);
        model.addAttribute("listHobby", listHobby);

        Criteria study = new Criteria(schoolName, 2, 1, 6);
        List<BoardVO> listStudy = boardService.getList(study);
        model.addAttribute("listStudy", listStudy);

        Criteria meal = new Criteria(schoolName, 3, 1, 6);
        List<BoardVO> listMeal = boardService.getList(meal);
        model.addAttribute("listMeal", listMeal);

        return "main";
    }

    @GetMapping("")
    public String start(@AuthenticationPrincipal MemberSecurityDTO memberSecurityDTO) {
        log.info("start page");
        if (memberSecurityDTO == null) {
            return "redirect:/member/login";
        } else {
            String schoolName = memberSecurityDTO.getAnySchoolAuthority();
            if (schoolName == null) {
                return "redirect:/member/chooseUni";
            } else {
                return "redirect:/" + schoolName.toLowerCase() + "/main";
            }
        }
    }
}
