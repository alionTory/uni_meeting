package io.github.aliontory.uni_meeting.dao;

import io.github.aliontory.uni_meeting.domain.Member;
import io.github.aliontory.uni_meeting.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberDAO {
    private final MemberRepository memberRepository;

    public boolean addRole(Long mid, String memberRole) {
        Optional<Member> result = memberRepository.findById(mid);
        if (result.isEmpty()) return false;
        Member member = result.get();
        member.addRole(memberRole);
        memberRepository.save(member);
        return true;
    }
}
