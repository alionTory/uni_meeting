package io.github.aliontory.uni_meeting.services;

import io.github.aliontory.uni_meeting.dao.MemberDAO;
import io.github.aliontory.uni_meeting.security.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImp implements MemberService {
    private final MemberDAO memberDAO;
    private final SchoolService schoolService;

    @Override
    public boolean addRole(Long mid, String memberRole) {
        // db에 해당 role 이 존재하는지 확인
        if (memberRole != "USER" && memberRole != "ADMIN") {
            Set<String> dbRoles = schoolService.getIdList().stream().map(String::toUpperCase).collect(Collectors.toSet());
            if (!dbRoles.contains(memberRole)) {
                log.info("유저에게 부여하려는 권한에 맞는 학교 정보가 db에 존재하지 않습니다.");
                return false;
            }
        }


        if (memberDAO.addRole(mid, memberRole)) {
            // db에 권한 추가가 성공하면 security 계정 정보 업데이트
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            List<GrantedAuthority> grantedAuthorities = new ArrayList<>(auth.getAuthorities());
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + memberRole));

            Object principal = auth.getPrincipal();
            if (principal instanceof MemberSecurityDTO) {
                MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) principal;
                memberSecurityDTO.setAuthorities(grantedAuthorities);
            }

            Authentication updatedAuth = new UsernamePasswordAuthenticationToken(principal, auth.getCredentials(), grantedAuthorities);
            SecurityContextHolder.getContext().setAuthentication(updatedAuth);

            MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("updated user authorities : ");
            log.info(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            log.info(currentMember.getAuthorities());

            return true;
        }

        return false;
    }

}
