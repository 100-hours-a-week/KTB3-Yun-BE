package KTB3.yun.Joongul.members;

import KTB3.yun.Joongul.common.auth.JwtTokenProvider;
import KTB3.yun.Joongul.common.dto.JwtToken;
import KTB3.yun.Joongul.global.utils.DatabaseCleanup;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.RefreshToken;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.members.repository.RefreshTokenRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    public void tearDown() {
        databaseCleanup.execute();
    }

    @Test
    @DisplayName("유효한 RT로 요청 시 새로운 AT와 RT 재발급에 성공한다")
    void 유효한_RT이면_토큰_재발급_성공() {
        Member member = saveMember();
        String validRefreshToken = createAndSaveRefreshToken(member);
        RefreshToken oldRefreshToken = refreshTokenRepository.findByRefreshTokenAndRevokedFalse(validRefreshToken).orElseThrow();
        Long oldTokenId = oldRefreshToken.getTokenId();

        given().log().all()
                .cookie("refreshToken", validRefreshToken)
                .when()
                .post("/token")
                .then().log().all()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        boolean isOldTokenExists = refreshTokenRepository.existsById(oldTokenId);
        assertFalse(isOldTokenExists);
    }

    @Test
    @DisplayName("RT 쿠키가 없으면 재발급 요청 시 TOKEN_NOT_FOUND(404) 오류가 발생한다")
    void RT_쿠키_없으면_TOKEN_NOT_FOUND() {
        given().log().all()
                .when()
                .post("/token")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo("존재하지 않는 리프레시 토큰입니다."));
    }

    @Test
    @DisplayName("유효하지 않은 형식의 RT로 재발급 요청 시 INVALID_TOKEN(401) 오류가 발생한다")
    void 유효하지_않은_형식의_RT는_INVALID_TOKEN() {
        given().log().all()
                .cookie("refreshToken", "not.valid.token")
                .when()
                .post("/token")
                .then().log().all()
                .statusCode(401)
                .body("message", equalTo("유효하지 않은 토큰입니다"));
    }

    @Test
    @DisplayName("DB에 존재하지 않는 RT로 재발급 요청 시 TOKEN_NOT_FOUND(404) 오류가 발생한다")
    void DB에_없는_RT는_TOKEN_NOT_FOUND() {
        Member member = saveMember();
        JwtToken jwtToken = jwtTokenProvider.generateJwt(member.getEmail(),
                member.getRoles().stream().map(SimpleGrantedAuthority::new).toList());

        given().log().all()
                .cookie("refreshToken", jwtToken.getRefreshToken())
                .when()
                .post("/token")
                .then().log().all()
                .statusCode(404)
                .body("message", equalTo("존재하지 않는 리프레시 토큰입니다."));
    }

    @Test
    @DisplayName("만료된 RT로 재발급 요청 시 401 오류가 발생한다")
    void 만료된_RT로_재발급_요청_시_실패() {
        Member member = saveMember();
        JwtToken jwtToken = jwtTokenProvider.generateJwt(member.getEmail(),
                member.getRoles().stream().map(SimpleGrantedAuthority::new).toList());

        RefreshToken expiredRefreshToken = RefreshToken.builder()
                .refreshToken(jwtToken.getRefreshToken())
                .member(member)
                .createdAt(System.currentTimeMillis() - 10000000)
                .expiresAt(1000L)
                .revoked(false)
                .build();
        refreshTokenRepository.save(expiredRefreshToken);

        given().log().all()
                .cookie("refreshToken", expiredRefreshToken.getRefreshToken())
                .when()
                .post("/token")
                .then().log().all()
                .statusCode(401)
                .body("message", equalTo("만료된 토큰입니다"));
    }


    private Member saveMember() {
        return memberRepository.save(Member.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("pw"))
                .nickname("tester")
                .roles(List.of("ROLE_USER"))
                .isDeleted(false)
                .build());
    }

    private String createAndSaveRefreshToken(Member member) {
        JwtToken jwtToken = jwtTokenProvider.generateJwt(member.getEmail(),
                member.getRoles().stream().map(SimpleGrantedAuthority::new).toList());

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(jwtToken.getRefreshToken())
                .member(member)
                .createdAt(System.currentTimeMillis())
                .expiresAt(jwtToken.getRefreshTokenExpireTime())
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return jwtToken.getRefreshToken();
    }
}
