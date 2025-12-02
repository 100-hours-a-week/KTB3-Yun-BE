package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.auth.JwtTokenProvider;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.dto.SignUpRequestDto;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.members.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

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
    @DisplayName("회원가입 시 이메일이 중복이면 DUPLICATE_EMAIL 예외를 던지고 save가 호출되지 않는다")
    void 회원가입_시_중복_이메일이면_DUPLICATE_EMAIL_예외() {
        //given
        SignUpRequestDto dto = new SignUpRequestDto("test@test.com", "Test111!", "Test111!",
                "테스트1", "");
        given(memberRepository.existsByEmail(dto.getEmail())).willReturn(true);
        given(memberRepository.existsByNickname(dto.getNickname())).willReturn(false);

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.signup(dto));
        assertEquals(ErrorCode.DUPLICATE_EMAIL, ex.getErrorCode());
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원가입 시 닉네임이 중복이면 DUPLICATE_NICKNAME 예외를 던지고 save가 호출되지 않는다")
    void 회원가입_시_중복_닉네임이면_DUPLICATE_NICKNAME_예외 () {
        //given
        SignUpRequestDto dto = new SignUpRequestDto("test@test.com", "Test111!", "Test111!",
                "테스트1", "");
        given(memberRepository.existsByEmail(dto.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(dto.getNickname())).willReturn(true);

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.signup(dto));
        assertEquals(ErrorCode.DUPLICATE_NICKNAME, ex.getErrorCode());
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원가입 시 비밀번호와 비밀번호 확인이 다르면 NOT_SAME_WITH_CONFIRM 예외를 던지고 save가 호출되지 않는다")
    void 회원가입_시_비밀번호_비밀번호확인_다르면_NOT_SAME_WITH_CONFIRM_예외 () {
        //given
        SignUpRequestDto dto = new SignUpRequestDto("test@test.com", "Test111!", "123",
                "테스트1", "");
        given(memberRepository.existsByEmail(dto.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(dto.getNickname())).willReturn(false);

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.signup(dto));
        assertEquals(ErrorCode.NOT_SAME_WITH_CONFIRM, ex.getErrorCode());
        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("유효성 검사를 통과한 경우, 회원가입을 시도하면 save를 한 번 호출한다")
    void 유효성_검사_통과_후_회원가입을_시도() {
        //given
        SignUpRequestDto dto = new SignUpRequestDto("test@test.com", "Test111!", "Test111!",
                "테스트1", "");
        given(memberRepository.existsByEmail(dto.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(dto.getNickname())).willReturn(false);

        //when
        memberService.signup(dto);

        //then
        verify(memberRepository, times(1)).save(any());
    }

    @Disabled
    @Test
    void getMemberInfo() {
    }

    @Disabled
    @Test
    void updateMemberInfo() {
    }

    @Disabled
    @Test
    void modifyPassword() {
    }

    @Disabled
    @Test
    void withdraw() {
    }

    @Disabled
    @Test
    void login() {
    }

    @Disabled
    @Test
    void logout() {
    }

    @Disabled
    @Test
    void isCorrectMember() {
    }

    @Disabled
    @Test
    void findIdByEmail() {
    }

    @Disabled
    @Test
    void isValidPassword() {
    }
}