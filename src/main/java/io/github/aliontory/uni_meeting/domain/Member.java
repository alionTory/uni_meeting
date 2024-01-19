package io.github.aliontory.uni_meeting.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString(exclude = "roleSet")
@EntityListeners(value = {AuditingEntityListener.class})  // regdate, updatedate 자동 입력 기능에 필요
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;
    private String email;
    private String name;
    @ColumnDefault("false")
    private boolean del;

    /*
    elementcollection -> db의 1 : N 관계에서 N이 독립적으로 쓰이지 않고
    단순한 형태인 경우, onetomany가 아닌 elementcollection 사용

    fetchtype lazy -> Member 엔티티를 사용하지만 roleSet 필드는 사용하지 않는 경우,
    roleSet필드는 가져올 필요가 없음.
    lazy로 설정하면 roleSet을 사용하기 전까지 roleset을 가져오는 쿼리는 실행되지 않음.
    거의 항상 쓰이는 필드라면 lazy가 아닌 eager로 설정할 것. 쿼리가 두 번 실행되는 것을 막을 수 있음.

    Builder.Default -> 빌더 패턴에서 필드의 기본값을 설정하는 역할.
    여기서는 new HashSet 이 기본값이 된다.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private Set<String> roleSet = new HashSet<>();
    @CreatedDate
    private String regdate;
    @LastModifiedDate
    private String updatedate;

    public void addRole(String memberRole) {
        this.roleSet.add(memberRole);
    }
}
