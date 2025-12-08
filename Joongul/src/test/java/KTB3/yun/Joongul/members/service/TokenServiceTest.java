package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.auth.JwtTokenProvider;
import KTB3.yun.Joongul.common.dto.JwtToken;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.RefreshToken;
import KTB3.yun.Joongul.members.dto.RefreshTokenResponseDto;
import KTB3.yun.Joongul.members.repository.RefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private TokenService tokenService;

    @Test
    @DisplayName("유효하지 않은 refreshToken일 경우 INVALID_TOKEN 예외를 던진다")
    void 유효하지_않은_refreshToken이면_INVALID_TOKEN_예외 () {
        //given
        String refreshToken = "invalid";
        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(false);

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> tokenService.getNewToken(refreshToken));
        assertEquals(ErrorCode.INVALID_TOKEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("해당 refreshToken이 DB에 없거나 revoke=true라면 TOKEN_NOT_FOUND 예외를 던진다")
    void 존재하지_않는_토큰이거나_revoked라면_TOKEN_NOT_FOUND_예외 (){
        //given
        String refreshToken = "token_not_found";
        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(refreshTokenRepository.findByRefreshTokenAndRevokedFalse(refreshToken)).willReturn(Optional.empty());

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class,
                () -> tokenService.getNewToken(refreshToken));
        assertEquals(ErrorCode.TOKEN_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("토큰의 만료시간이 지났다면 INVALID_TOKEN 예외를 던진다")
    void 만료된_토큰이라면_INVALID_TOKEN_예외 () {
        //given
        String refreshToken = "expired";
        Member member = Member.builder().email("test").build(); //서비스 로직 내부 코드 순서 변경 시 NPE로 인한 테스트 깨짐 방지
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .createdAt(1)
                .expiresAt(1)
                .member(member)
                .build();
        given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
        given(refreshTokenRepository.findByRefreshTokenAndRevokedFalse(refreshToken)).willReturn(Optional.of(refreshTokenEntity));

        //when
        //then
        ApplicationException ex = assertThrows(ApplicationException.class, () -> tokenService.getNewToken(refreshToken));
        assertEquals(ErrorCode.EXPIRED_TOKEN, ex.getErrorCode());
    }

    @Test
    @DisplayName("유효한 refreshToken인 경우 새로운 JWT를 발급하고 기존의 RT를 삭제, 새로운 RT를 저장한 뒤 AT와 함께 반환한다")
    void 유효한_refreshToken이면_새로운_RT_발급_저장_후_AT와_함께_발급 () {
        //given
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        String oldRefreshToken = "valid";
        String email = "test";
        String password = "test";
        String nickname = "test";
        List<String> roles = List.of("ROLE_USER");
        Member member = Member.builder()
                .memberId(1L)
                .email(email)
                .password(password)
                .nickname(nickname)
                .roles(roles).build();

        RefreshToken oldRefreshTokenEntity = RefreshToken.builder()
                .refreshToken(oldRefreshToken)
                .createdAt(System.currentTimeMillis())
                .expiresAt(System.currentTimeMillis() + 1000L)
                .member(member)
                .revoked(false).build();

        JwtToken jwtToken = JwtToken.builder()
                        .grantType("Bearer")
                        .accessToken("new_access")
                        .refreshToken("new_refresh")
                        .refreshTokenExpireTime(System.currentTimeMillis() + 1000L)
                        .build();
        given(jwtTokenProvider.validateToken(oldRefreshToken)).willReturn(true);
        given(refreshTokenRepository.findByRefreshTokenAndRevokedFalse(oldRefreshToken)).willReturn(Optional.of(oldRefreshTokenEntity));
        given(jwtTokenProvider.generateJwt(anyString(), any())).willReturn(jwtToken);

        //when
        RefreshTokenResponseDto dto = tokenService.getNewToken(oldRefreshToken);

        //then
        then(refreshTokenRepository).should(times(1)).delete(oldRefreshTokenEntity);
        then(refreshTokenRepository).should(times(1)).save(captor.capture());

        assertEquals(jwtToken.getAccessToken(), dto.getAccessToken());
        assertEquals(captor.getValue().getRefreshToken(), dto.getRefreshToken());
        assertEquals(captor.getValue().getExpiresAt(), dto.getRefreshTokenExpireTime());
    }
}