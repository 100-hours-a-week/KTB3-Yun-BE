package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.auth.JwtTokenProvider;
import KTB3.yun.Joongul.common.dto.JwtToken;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.MemberDetails;
import KTB3.yun.Joongul.members.domain.RefreshToken;
import KTB3.yun.Joongul.members.dto.RefreshTokenResponseDto;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.members.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public TokenService(RefreshTokenRepository refreshTokenRepository, MemberRepository memberRepository,
                        JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public RefreshTokenResponseDto getNewToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ApplicationException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }

        RefreshToken oldRefreshToken = refreshTokenRepository.findByRefreshTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        long now = System.currentTimeMillis();
        long refreshTokenExpireTime = oldRefreshToken.getCreatedAt() + oldRefreshToken.getExpiresAt();

        if (refreshTokenExpireTime <= now) {
            throw new ApplicationException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }

        Member member = oldRefreshToken.getMember();
        String email = member.getEmail();
        MemberDetails memberDetails = new MemberDetails(member);
        Collection<? extends GrantedAuthority> authorities = memberDetails.getAuthorities();

        JwtToken jwtToken = jwtTokenProvider.generateJwt(email, authorities);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .refreshToken(jwtToken.getRefreshToken())
                .createdAt(System.currentTimeMillis())
                .expiresAt(jwtToken.getRefreshTokenExpireTime())
                .member(member)
                .revoked(false)
                .build();

        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        refreshTokenRepository.save(newRefreshToken);


        return new RefreshTokenResponseDto(jwtToken.getAccessToken(),
                jwtToken.getRefreshToken(), jwtToken.getRefreshTokenExpireTime());
    }

    public String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return "not_found";
    }
}
