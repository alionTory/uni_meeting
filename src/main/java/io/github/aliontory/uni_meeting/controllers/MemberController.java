package io.github.aliontory.uni_meeting.controllers;

import io.github.aliontory.uni_meeting.beans.vo.SchoolVO;
import io.github.aliontory.uni_meeting.security.MemberSecurityDTO;
import io.github.aliontory.uni_meeting.services.MemberService;
import io.github.aliontory.uni_meeting.services.SchoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberController {
    private final MemberService memberService;
    private final SchoolService schoolService;

    @GetMapping("/login")
    public void loginGET(String error, String logout) {
        log.info("login get..............");
        log.info("logout: " + logout);

        if (logout != null) {
            log.info("user logout.................");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chooseUni")
    public void chooseUni(Model model) {
        List<SchoolVO> schoolList = schoolService.getList();
        model.addAttribute("schoolList", schoolList);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/chooseUni")
    public String chooseUni(String school) {
        MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        memberService.addRole(currentMember.getMid(), school.toUpperCase());
        return "redirect:/";
    }
}
