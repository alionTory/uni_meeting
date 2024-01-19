package io.github.aliontory.uni_meeting.security;

import io.github.aliontory.uni_meeting.domain.Member;
import io.github.aliontory.uni_meeting.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public MemberSecurityDTO loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("userRequest....");
        log.info(userRequest);

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();
        String nameAttributeKey = clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        log.info("NAME : " + clientName);  // ex -> NAME : KAKAO

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();
        log.info("paramMap : ");
        log.info(paramMap);

        String email = null;
        String name = null;

        switch (clientName) {
            case "kakao" -> {
                email = getKakaoEmail(paramMap);
                name = getKakaoNickname(paramMap);
            }
        }

        log.info(email);

        log.info("generating MemberSecurityDTO");
        return generateDTO(email, name, paramMap, nameAttributeKey);
    }

    private String getKakaoEmail(Map<String, Object> paramMap) {
        Object value = paramMap.get("kakao_account");
        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String) accountMap.get("email");
        return email;
    }

    private String getKakaoNickname(Map<String, Object> paramMap) {
        Object value = paramMap.get("properties");
        LinkedHashMap accountMap = (LinkedHashMap) value;
        String nickname = (String) accountMap.get("nickname");
        return nickname;
    }

    private MemberSecurityDTO generateDTO(String email, String name, Map<String, Object> params, String nameAttributeKey) {
        Optional<Member> result = memberRepository.findByEmail(email);

        if (result.isEmpty()) {
            // 해당 이메일을 가진 사용자가 없다면 회원 추가
            log.info("email not exists in db");
            log.info("insert new member");
            Member member = Member.builder()
                    .email(email)
                    .name(name)
                    .del(false)
                    .build();
            member.addRole("USER");
            memberRepository.save(member);

            log.info("creating MemberSecurityDTO");
            MemberSecurityDTO memberSecurityDTO = new MemberSecurityDTO(member.getMid(), member.getEmail(), member.getName(), false,
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")), params, nameAttributeKey);
            memberSecurityDTO.setProps(params);

            return memberSecurityDTO;
        } else {
            log.info("email exists in db");
            log.info("getting member from db");
            Member member = result.get();
            log.info("creating MemberSecurityDTO");
            MemberSecurityDTO memberSecurityDTO =
                    new MemberSecurityDTO(
                            member.getMid(),
                            member.getEmail(),
                            member.getName(),
                            member.isDel(),
                            member.getRoleSet().stream()
                                    .map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole))
                                    .collect(Collectors.toList()),
                            params,
                            nameAttributeKey
                    );
            log.info("authorities : " + memberSecurityDTO.getAuthorities());
            return memberSecurityDTO;
        }
    }

}
