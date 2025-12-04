package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.auth.JwtTokenProvider;
import KTB3.yun.Joongul.common.dto.JwtToken;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.MemberDetails;
import KTB3.yun.Joongul.members.domain.RefreshToken;
import KTB3.yun.Joongul.members.dto.*;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.members.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    private Member 정상_회원() {
        return Member.builder()
                .memberId(1L)
                .email("email")
                .password("password")
                .nickname("nickname")
                .isDeleted(false)
                .roles(List.of("ROLE_USER")).build();
    }

    private Member 탈퇴_회원() {
        return Member.builder()
                .memberId(1L)
                .email("email")
                .password("password")
                .nickname("nickname")
                .isDeleted(true)
                .roles(List.of("ROLE_USER")).build();
    }

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
        then(memberRepository).should(never()).save(any());
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
        then(memberRepository).should(never()).save(any());
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
        then(memberRepository).should(never()).save(any());
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
        then(memberRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("조회하려는 회원이 존재하지 않을 때 NOT_FOUND 예외를 던진다")
    void 회원조회_시_없는_회원인_경우_NOT_FOUND_예외 () {
        //given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.getMemberInfo(memberId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("조회하려는 회원이 탈퇴했을 때 NOT_FOUND 예외를 던진다")
    void 회원조회_시_탈퇴한_회원인_경우_NOT_FOUND_예외 () {
        //given
        Long memberId = 1L;
        Member member = 탈퇴_회원();
        given(memberRepository.findById(memberId))
                .willReturn(Optional.of(member));

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.getMemberInfo(memberId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("조회하려는 회원이 존재하고 탈퇴하지 않았다면 해당 회원의 정보로 MemberInfoResponseDto를 반환한다")
    void 회원조회_시_회원이_존재하고_미탈퇴인_경우_회원정보_DTO_반환() {
        //given
        Long memberId = 1L;
        Member member = 정상_회원();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when
        MemberInfoResponseDto dto = memberService.getMemberInfo(memberId);

        //then
        then(memberRepository).should(times(1)).findById(memberId);
        assertEquals(member.getMemberId(), dto.getMemberId());
        assertEquals(member.getEmail(), dto.getEmail());
        assertEquals(member.getNickname(), dto.getNickname());
        assertEquals(member.getProfileImage(), dto.getProfileImage());
    }

    @Test
    @DisplayName("회원정보 수정 시 닉네임이 중복인 경우 DUPLICATE_NICKNAME 예외를 던지고 findById가 호출되지 않는다")
    void 회원정보_수정_시_중복_닉네임이면_DUPLICATE_NICKNAME_예외 () {
        //given
        MemberInfoUpdateRequestDto dto = new MemberInfoUpdateRequestDto("테스트1", "");
        given(memberRepository.existsByNickname(dto.getNickname())).willReturn(true);


        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> memberService.updateMemberInfo(dto, 1L));
        assertEquals(ErrorCode.DUPLICATE_NICKNAME, ex.getErrorCode());
        then(memberRepository).should(never()).findById(any());
    }

    @Test
    @DisplayName("정보를 수정하려는 회원이 존재하지 않을 때 NOT_FOUND 예외를 던진다")
    void 회원정보_수정_시_회원이_존재하지_않으면_NOT_FOUND_예외 () {
        //given
        Long memberId = 1L;
        MemberInfoUpdateRequestDto dto = new MemberInfoUpdateRequestDto("테스트1", "");
        given(memberRepository.existsByNickname(dto.getNickname())).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> memberService.updateMemberInfo(dto, memberId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("정보를 수정하려는 회원이 탈퇴했을 때 NOT_FOUND 예외를 던진다")
    void 회원정보_수정_시_회원이_탈퇴했다면_NOT_FOUND_예외 () {
        //given
        Long memberId = 1L;
        Member member = 탈퇴_회원();
        MemberInfoUpdateRequestDto dto = new MemberInfoUpdateRequestDto("테스트1", "");
        given(memberRepository.existsByNickname(dto.getNickname())).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.updateMemberInfo(dto, memberId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("닉네임 중복 검사를 통과한 뒤 정보를 수정하려는 회원이 존재하고 탈퇴하지 않았다면 해당 회원의 닉네임과 프로필 사진이 성공적으로 수정된다")
    void 회원정보_수정_시_유효성_통과_및_활성_회원이면_닉네임과_프로필_사진_수정() {
        //given
        Long memberId = 1L;
        Member member = 정상_회원();
        MemberInfoUpdateRequestDto dto = new MemberInfoUpdateRequestDto("테스트1", "123");
        given(memberRepository.existsByNickname(dto.getNickname())).willReturn(false);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when
        assertEquals("nickname", member.getNickname()); //수정 전
        memberService.updateMemberInfo(dto, memberId);

        //then
        assertEquals(dto.getNickname(), member.getNickname());
        assertEquals(dto.getProfileImage(), member.getProfileImage());
    }

    @Test
    @DisplayName("기존에 사용하던 비밀번호로 변경 시도 시 USING_PASSWORD 예외를 던진다")
    void 비밀번호_변경_시_기존과_같으면_USING_PASSWORD_예외 () {
        //given
        Long memberId = 1L;
        Member member = 정상_회원();
        PasswordUpdateRequestDto dto = new PasswordUpdateRequestDto("123", "123");
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(dto.getPassword(), member.getPassword())).willReturn(true);

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.modifyPassword(dto, memberId));
        assertEquals(ErrorCode.USING_PASSWORD, ex.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 같지 않으면 변경 시도 시 NOT_SAME_WITH_CONFIRM 예외를 던진다")
    void 비밀번호와_비밀번호_확인이_같지_않으면_NOT_SAME_WITH_CONFIRM_예외 () {
        //given
        Long memberId = 1L;
        Member member = 정상_회원();
        PasswordUpdateRequestDto dto = new PasswordUpdateRequestDto("123", "1");
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.modifyPassword(dto, memberId));
        assertEquals(ErrorCode.NOT_SAME_WITH_CONFIRM, ex.getErrorCode());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 비밀번호 변경 요청 시 NOT_FOUND 예외를 던진다")
    void 존재하지_않는_사용자의_비밀번호_변경_요청_시_NOT_FOUND_예외 () {
        //given
        Long memberId = 1L;
        PasswordUpdateRequestDto dto = new PasswordUpdateRequestDto("123", "123");
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.modifyPassword(dto, memberId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("탈퇴한 사용자로 비밀번호 변경 요청 시 NOT_FOUND 예외를 던진다")
    void 탈퇴한_사용자의_비밀번호_변경_요청_시_NOT_FOUND_예외 () {
        //given
        Long memberId = 1L;
        Member member = 탈퇴_회원();
        PasswordUpdateRequestDto dto = new PasswordUpdateRequestDto("123", "123");
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(dto.getPassword(), member.getPassword())).willReturn(false);

        //when

        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.modifyPassword(dto, memberId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 변경 시 유효성 검사를 통과하고 탈퇴하지 않은 회원이라면 해당 회원의 비밀번호가 성공적으로 수정된다")
    void 유효성_검사_통과_및_미탈퇴_회원인_경우_비밀번호_변경() {
        //given
        Long memberId = 1L;
        Member member = 정상_회원();
        PasswordUpdateRequestDto dto = new PasswordUpdateRequestDto("123", "123");
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(dto.getPassword(), member.getPassword())).willReturn(false);
        given(passwordEncoder.encode(dto.getPassword())).willReturn(dto.getPassword());

        //when
        memberService.modifyPassword(dto, memberId);

        //then
        assertEquals(dto.getPassword(), member.getPassword());
    }

    @Test
    @DisplayName("존재하지 않는 회원이 탈퇴 요청을 할 시 NOT_FOUND 예외를 던진다")
    void 존재하지_않는_사용자가_탈퇴_요청_시_NOT_FOUND () {
        //given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.withdraw(memberId));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("존재하는 회원이 탈퇴 요청을 할 시 회원의 isDeleted가 true로 변경된다")
    void 존재하는_회원이_탈퇴_요청_시_회원_탈퇴 () {
        //given
        Long memberId = 1L;
        Member member = 정상_회원();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when
        assertFalse(member.getIsDeleted()); //변경 전
        memberService.withdraw(memberId);

        //then
        assertTrue(member.getIsDeleted());
    }

    @Test
    @DisplayName("탈퇴한 회원으로 로그인 요청을 보내면 NOT_FOUND")
    void 로그인_요청_시_탈퇴한_회원이면_NOT_FOUND_예외 () {
        //given
        LoginRequestDto dto = new LoginRequestDto("email", "password");
        Member member = 탈퇴_회원();
        given(memberRepository.findByEmail(dto.getEmail())).willReturn(Optional.of(member));

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.login(dto));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("로그인 요청을 보내면 authenticate(), generateToken(), save()가 한 번 실행된 뒤 LoginResponseDto를 반환한다")
    void 로그인_요청_시_authenticate_generateToken_save_실행되고_ResponseDto_반환() {
        //given
        Member member = 정상_회원();
        LoginRequestDto dto = new LoginRequestDto(member.getEmail(), member.getPassword());
        JwtToken token = JwtToken.builder()
                .grantType("1")
                .accessToken("a")
                .refreshToken("r")
                .refreshTokenExpireTime(1000L)
                .build();

        MemberDetails memberDetails = new MemberDetails(member);

        Authentication auth = new UsernamePasswordAuthenticationToken(memberDetails,
                null, memberDetails.getAuthorities());
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willReturn(auth);
        given(jwtTokenProvider.generateToken(any(Authentication.class))).willReturn(token);

        //when
        LoginResponseDto resDto = memberService.login(dto);

        //then
        then(authenticationManager).should(times(1)).authenticate(any());
        then(jwtTokenProvider).should(times(1)).generateToken(any());
        then(refreshTokenRepository).should(times(1)).save(captor.capture());

        assertEquals("1", resDto.getGrantType());
        assertEquals("a", resDto.getAccessToken());
        assertEquals("r", resDto.getRefreshToken());
        assertEquals(1000L, resDto.getRefreshTokenExpireTime());
        assertEquals(member.getEmail(), resDto.getEmail());
        assertEquals(member.getNickname(), resDto.getNickname());

        RefreshToken refreshToken = captor.getValue();

        assertEquals(token.getRefreshToken(), refreshToken.getRefreshToken());
        assertEquals(token.getRefreshTokenExpireTime(), refreshToken.getExpiresAt());
        assertEquals(member, refreshToken.getMember());
        assertFalse(refreshToken.isRevoked());
    }

    @Test
    @DisplayName("로그아웃 요청을 보내면 deleteByRefreshToken이 refreshToken을 넘겨받아 한 번 실행된다")
    void 로그아웃_요청_시_deleteByRefreshToken_1번_실행 () {
        //given
        String refreshToken = "refreshToken";

        //when
        memberService.logout(refreshToken);

        //then
        then(refreshTokenRepository).should(times(1)).deleteByRefreshToken(refreshToken);
    }

    //isUsedPassword()는 통합 테스트에서 검증
    @Disabled
    @Test
    @DisplayName("존재하지 않는 회원의 memberId를 받으면 NOT_FOUND 예외를 던진다")
    void isUsedPassword가_존재하지_않는_회원의_memberId_받으면_NOT_FOUND () {
        //given
        Long memberId = 1L;
        String password = "password";
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> memberService.isUsedPassword(memberId, password));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Disabled
    @Test
    @DisplayName("정상적인 회원의 memberId와 password를 받으면 memberId로 회원을 조회한 뒤 passwordEncoder.matches를 1번 실행한다.")
    void isUsedPassword가_정상적인_회원의_memberId와_password를_받음 () {
        //given
        Long memberId = 1L;
        String rawPassword = "raw";
        String savedPassword = "saved";
        Member member = Member.builder().password(savedPassword).build();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        //when
        memberService.isUsedPassword(memberId, rawPassword);

        //then
        then(memberRepository).should(times(1)).findById(memberId);
        then(passwordEncoder).should(times(1)).matches(rawPassword, member.getPassword());
    }
}