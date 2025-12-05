package KTB3.yun.Joongul.common.auth;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
class JwtTokenProviderTest {

    @Mock
    MemberRepository memberRepository;

    JwtTokenProvider jwtTokenProvider;

    String secretKey;

    private String 테스트용_토큰_생성(String subject, Date time, String authClaim) {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));

        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(subject)
                .expiration(time)
                .signWith(key);

        if (authClaim != null) {
            jwtBuilder.claim("auth",  authClaim);
        }

        return jwtBuilder.compact();
    }

    @BeforeEach
    void setUp() {
        String key = "xptmxmzhemwkrtjdgksmsrjwlsWKdjfudnsrjrkxdmsepwoalsmsdlTsmsrjrkxdo";
        secretKey = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));

        jwtTokenProvider = new JwtTokenProvider(secretKey, memberRepository);
    }

    @Test
    @DisplayName("getAuthentication()에 전달된 AT에 권한 정보가 없으면 INVALID_TOKEN 예외를 던진다")
    void AT에_권한_정보가_없으면_INVALID_TOKEN_예외() {
        //given
        Date expTime = new Date(System.currentTimeMillis() + 1000 * 60);
        String accessToken = 테스트용_토큰_생성("email", expTime,null);

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> jwtTokenProvider.getAuthentication(accessToken));
        assertEquals(ErrorCode.INVALID_TOKEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("유효한 AT에서 Authentication 객체를 정상적으로 조회한다")
    void AT가_유효하다면_Authentication_객체를_반환 () {
        //given
        Date expTime = new Date(System.currentTimeMillis() + 1000 * 60);
        String accessToken = 테스트용_토큰_생성("email", expTime, "ROLE_USER");

        //when
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        //then
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);

        assertEquals("email", authentication.getName());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("validateToken()에서 잘못된 서명으로 검증 시(SecurityException) 유효하지 않은 토큰 log를 남기고 false를 반환한다")
    void 잘못된_서명으로_검증하면_SecurityException(CapturedOutput output) {
        //given
        String wrongKey = "msrjrkxdgksmsalsojwlsWKdejrkxswkrdmstmxmzhemrmsdlTtjdpwoxpjfudnsr";
        String wrongSecretKey = Base64.getEncoder().encodeToString(wrongKey.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .subject("email")
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(wrongSecretKey)))
                .compact();

        //when
        boolean result = jwtTokenProvider.validateToken(token);

        //then
        assertThat(output.getOut()).contains("유효하지 않은 토큰입니다.");
        assertThat(output.getOut()).contains("ERROR");
        assertFalse(result);
    }

    @Test
    @DisplayName("validateToken()에서 형식이 잘못된 토큰일 경우(MalformedJwtException) 유효하지 않은 토큰 log를 남기고 false를 반환한다")
    void 형식이_잘못된_토큰이면_MalformedJwtException(CapturedOutput output) {
        //given
        String malformedToken = "Bearer 진짜 이상한 토큰";

        //when
        boolean result = jwtTokenProvider.validateToken(malformedToken);

        //then
        assertThat(output.getOut()).contains("유효하지 않은 토큰입니다.");
        assertThat(output.getOut()).contains("ERROR");
        assertFalse(result);
    }

    @Test
    @DisplayName("validateToken()에서 만료된 토큰일 경우(ExpiredJwtException) 만료된 토큰입니다 log를 남기고 false를 반환한다")
    void 만료된_토큰이면_ExpiredJwtException(CapturedOutput output) {
        //given
        Date pastTime = new Date(System.currentTimeMillis() - 1000 * 60);
        String expiredToken = 테스트용_토큰_생성("email", pastTime, "ROLE_USER");

        //when
        boolean result = jwtTokenProvider.validateToken(expiredToken);

        //then
        assertThat(output.getOut()).contains("만료된 토큰입니다.");
        assertThat(output.getOut()).contains("ERROR");
        assertFalse(result);
    }

    @Test
    @DisplayName("validateToken()에서 Claims가 null일 경우(IllegalArgumentException) Claims가 비어 있다는 log를 남기고 false를 반환한다")
    void Claims가_null이면_IllegalArgumentException(CapturedOutput output) {
        //when
        boolean resultNull = jwtTokenProvider.validateToken(null);

        //then
        assertThat(output.getOut()).contains("Claims가 비어 있습니다.");
        assertThat(output.getOut()).contains("ERROR");
        assertFalse(resultNull);
    }

    @Test
    @DisplayName("validateToken()에서 Claims가 비어 있을 경우(IllegalArgumentException) Claims가 비어 있다는 log를 남기고 false를 반환한다")
    void Claims가_비어_있으면_IllegalArgumentException(CapturedOutput output) {
        //when
        boolean resultEmpty = jwtTokenProvider.validateToken("");

        //then
        assertThat(output.getOut()).contains("Claims가 비어 있습니다.");
        assertThat(output.getOut()).contains("ERROR");
        assertFalse(resultEmpty);
    }

    @Test
    @DisplayName("유효한 토큰일 경우 true를 반환한다")
    void 유효한_토큰이면_true_반환() {
        //given
        Date expTime = new Date(System.currentTimeMillis() + 1000 * 60);
        String validToken = 테스트용_토큰_생성("email", expTime, "ROLE_USER");

        //when
        boolean result = jwtTokenProvider.validateToken(validToken);

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("extractMemberId()로 전달된 요청의 헤더가 존재하지 않으면 INVALID_TOKEN 예외를 던진다")
    void 헤더가_존재하지_않으면_INVALID_TOKEN_예외() {
        //given
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn(null);

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> jwtTokenProvider.extractMemberId(request));
        assertEquals(ErrorCode.INVALID_TOKEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("extractMemberId()로 전달된 요청의 헤더가 Bearer로 시작하지 않으면 INVALID_TOKEN 예외를 던진다")
    void 헤더가_Bearer로_시작하지_않으면_INVALID_TOKEN_예외() {
        //given
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Different some_token");

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> jwtTokenProvider.extractMemberId(request));
        assertEquals(ErrorCode.INVALID_TOKEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("extractMemberId()로 전달된 요청 헤더에 담긴 토큰이 유효하지 않다면 INVALID_TOKEN 예외를 던진다")
    void 헤더에_담긴_토큰이_유효하지_않으면_INVALD_TOKEN_예외() {
        //given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String invalidToken = "invalid_token";
        given(request.getHeader("Authorization")).willReturn("Bearer " + invalidToken);

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> jwtTokenProvider.extractMemberId(request));
        assertEquals(ErrorCode.INVALID_TOKEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("extractMemberId()로 전달된 토큰이 유효하지만 존재하지 않는 회원이라면 NOT_FOUND 예외를 던진다")
    void 토큰은_유효하지만_존재하지_않는_회원이면_NOT_FOUND_예외() {
        //given
        Date time = new Date(System.currentTimeMillis() + 1000 * 60);
        String validToken = 테스트용_토큰_생성("email", time,"ROLE_USER");

        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Bearer " + validToken);

        given(memberRepository.findByEmail("email")).willReturn(Optional.empty());

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> jwtTokenProvider.extractMemberId(request));
        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("모든 조건이 정상이면 memberId를 반환한다")
    void 정상적인_호출이면_memberId를_반환() {
        //given
        Date time = new Date(System.currentTimeMillis() + 1000 * 60);
        String validToken = 테스트용_토큰_생성("email", time,"ROLE_USER");
        Long expectedId = 1L;

        HttpServletRequest request = mock(HttpServletRequest.class);
        given(request.getHeader("Authorization")).willReturn("Bearer " + validToken);

        Member member = Member.builder()
                .memberId(expectedId)
                .email("email")
                .build();
        given(memberRepository.findByEmail("email")).willReturn(Optional.of(member));

        //when
        Long result = jwtTokenProvider.extractMemberId(request);

        //then
        assertEquals(expectedId, result);
    }
}