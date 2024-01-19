package io.github.aliontory.uni_meeting.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@ToString
public class TestMemberDTO extends MemberSecurityDTO implements UserDetails {
    private String password;


    public TestMemberDTO(Long mid, String email, String name, boolean del, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        super(mid, email, name, del, authorities, null, null);
        this.password = password;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return Long.toString(this.getMid());
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.isDel();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
