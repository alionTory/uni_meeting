package io.github.aliontory.uni_meeting.security;

import io.github.aliontory.uni_meeting.domain.Member;
import io.github.aliontory.uni_meeting.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserDebuggingService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: " + username);

        Optional<Member> result = memberRepository.findById(Long.parseLong(username));

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("username not found.");
        }

        Member member = result.get();
        UserDetails user = new TestMemberDTO(
                member.getMid(), member.getEmail(), member.getName(), member.isDel(), passwordEncoder.encode("testpw"),
                member.getRoleSet().stream()
                        .map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole)).toList());

        log.info("Test User : ");
        log.info(user);

        return user;
    }
}
