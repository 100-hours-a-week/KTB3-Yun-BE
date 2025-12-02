package KTB3.yun.Joongul.common.auth;

import KTB3.yun.Joongul.common.dto.JwtToken;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final MemberRepository memberRepository;
    private static final long ACCESS_TOKEN_VALID_TIME = Duration.ofMinutes(1).toMillis();
    private static final long REFRESH_TOKEN_VALID_TIME = Duration.ofDays(7).toMillis();

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, MemberRepository memberRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.memberRepository = memberRepository;
    }

    public JwtToken generateJwt(String email, Collection<? extends GrantedAuthority> authorities) {
        long now = (new Date()).getTime();

        Date accessTokenExpirationTime = new Date(now + ACCESS_TOKEN_VALID_TIME);
        Date refreshTokenExpirationTime = new Date(now + REFRESH_TOKEN_VALID_TIME);

        String accessToken = Jwts.builder()
                .subject(email)
                .claim("auth", authorities)
                .expiration(accessTokenExpirationTime)
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .expiration(refreshTokenExpirationTime)
                .signWith(key)
                .compact();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpireTime(REFRESH_TOKEN_VALID_TIME)
                .build();
    }

    public JwtToken generateToken(Authentication authentication) {

        String email = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return generateJwt(email, authorities);
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = extractClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new ApplicationException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 토큰입니다.");
            return false;
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰입니다.");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰입니다.");
            return false;
        } catch (IllegalArgumentException e) {
            log.error("Claims가 비어 있습니다.");
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public Long extractMemberId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            throw new ApplicationException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
        String token = authHeader.substring(7);

        if (!validateToken(token)) {
            throw new ApplicationException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }

        Claims claims = extractClaims(token);
        String username = claims.getSubject();
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        return member.getMemberId();
    }

    private Claims extractClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
