package io.github.aliontory.uni_meeting.repository;

import io.github.aliontory.uni_meeting.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
//    @EntityGraph(attributePaths = "roleSet")
//    @Query("select m from Member m where m.mid = :mid and m.social = false")
//    Optional<Member> getWithRoles(String mid);

    /*
    @EntityGraph 를 메소드에 붙이면, roleSet을 lazy로 설정했더라도
    이 메소드가 실행될 땐 eager처럼 실행됨(쿼리 구문에 join이 붙음)
     */
    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findById(Long mid);
    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findByEmail(String email);
}
