package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.auth.JwtTokenProvider;
import KTB3.yun.Joongul.members.dto.SignUpRequestDto;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.members.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 시 이메일이 중복이면 예외를 던진다")
    void 중복_이메일이면_예외 () {

    }

    @Test
    @DisplayName("회원가입을 시도하면 save를 한 번 호출한다")
    void 회원가입을_시도() {
        //given
        SignUpRequestDto dto = new SignUpRequestDto("1", "1", "1",
                "1", "1");
        //when
        memberService.signup(dto);
        //then
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    void getMemberInfo() {
    }

    @Test
    void updateMemberInfo() {
    }

    @Test
    void modifyPassword() {
    }

    @Test
    void withdraw() {
    }

    @Test
    void login() {
    }

    @Test
    void logout() {
    }

    @Test
    void isCorrectMember() {
    }

    @Test
    void findIdByEmail() {
    }

    @Test
    void isValidPassword() {
    }
}