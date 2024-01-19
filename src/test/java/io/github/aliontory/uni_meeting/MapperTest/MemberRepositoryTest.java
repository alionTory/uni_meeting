package io.github.aliontory.uni_meeting.MapperTest;

import io.github.aliontory.uni_meeting.domain.Member;
import io.github.aliontory.uni_meeting.repository.MemberRepository;
import io.github.aliontory.uni_meeting.domain.MemberRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMembers() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Member member = Member.builder()
                    .email("email" + i + "@aaa.bbb")
                    .name("더미사용자" + i)
                    .build();

            member.addRole("USER");

            if (i >= 90) {
                member.addRole("ADMIN");
            }

            memberRepository.save(member);
        });
    }

    @Test
    public void addAuth() {
        IntStream.rangeClosed(1, 10).forEach(i -> {
            Member member = memberRepository.findById((long) i).get();
            member.addRole("KONKUK");
            memberRepository.save(member);
        });
    }

    @Test
    public void deleteMember(){
        memberRepository.deleteById(107L);
    }

}
