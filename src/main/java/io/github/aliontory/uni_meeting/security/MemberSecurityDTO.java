package io.github.aliontory.uni_meeting.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class MemberSecurityDTO implements OAuth2User {
    private long mid;
    private String email;
    private String name;
    private boolean del;

    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> props;  // 소셜 로그인 정보
    String nameAttributeKey;

    public MemberSecurityDTO(Long mid, String email, String name, boolean del,
                             Collection<? extends GrantedAuthority> authorities,
                             Map<String, Object> attributes, String nameAttributeKey) {
        this.mid = mid;
        this.email = email;
        this.name = name;
        this.del = del;
        this.authorities = authorities;
        this.props = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    public boolean hasAuthorities(String authorityName) {
        return authorities.stream().map(GrantedAuthority::getAuthority)
                .anyMatch((authority) -> authority.equals("ROLE_" + authorityName));
    }

    public String getAnySchoolAuthority(){

        for(GrantedAuthority authority:authorities){
            String authorityName = authority.getAuthority();
            if(!authorityName.equals("ROLE_ADMIN")&&!authorityName.equals("ROLE_USER")){
                return authorityName.substring(5);
            }
        }
        return null;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return this.getProps();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }
}
