package KTB3.yun.Joongul.members;

import KTB3.yun.Joongul.global.utils.DatabaseCleanup;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.dto.LoginRequestDto;
import KTB3.yun.Joongul.members.dto.MemberInfoUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.PasswordUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.SignUpRequestDto;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.members.service.MemberService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String signUpAndLogin(String email, String password, String nickname) {
        given().contentType(ContentType.JSON)
                .body(new SignUpRequestDto(email, password, password,nickname, null))
                .post("/members");

        return given().contentType(ContentType.JSON)
                .body(new LoginRequestDto(email, password))
                .post("/members/session")
                .then().extract().jsonPath().getString("accessToken");
    }

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    public void tearDown() {
        databaseCleanup.execute();
    }

    @Test
    @DisplayName("회원가입 후 로그인하면 토큰을 발급받고, 해당 토큰으로 내 정보를 조회할 수 있다")
    void 회원가입_후_로그인_시_내_정보_조회_성공() {
        String accessToken = signUpAndLogin("test@test.com", "Test111!", "테스터");

        given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/members/me")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("data.email", equalTo("test@test.com"))
                .body("data.nickname", equalTo("테스터"));
    }

    @Test
    @DisplayName("토큰 없이 내 정보 API를 호출하면 401 에러가 발생한다")
    void 토큰_없이_내_정보_호출하면_401 () {
        given().log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/members/me")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("중복된 이메일로 가입을 시도하면 실패한다")
    void 중복_이메일로_가입하면_실패 () {
        SignUpRequestDto req1 = new SignUpRequestDto("test@test.com", "Test123!", "Test123!", "테스터1", null);
        SignUpRequestDto req2 = new SignUpRequestDto("test@test.com", "Test123!", "Test123!", "테스터2", null);

        memberService.signup(req1);

        given().log().all()
                .contentType(ContentType.JSON)
                .body(req2)
                .when()
                .post("/members")
                .then().log().all()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("중복된 닉네임으로 가입을 시도하면 실패한다")
    void 중복_닉네임으로_가입하면_실패 () {
        SignUpRequestDto req1 = new SignUpRequestDto("test@test.com", "Test123!", "Test123!", "테스터1", null);
        SignUpRequestDto req2 = new SignUpRequestDto("testtest@test.com", "Test123!", "Test123!", "테스터1", null);

        memberService.signup(req1);

        given().log().all()
                .contentType(ContentType.JSON)
                .body(req2)
                .when()
                .post("/members")
                .then().log().all()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 회원 정보 수정을 요청하면 401 에러가 발생한다")
    void 비로그인_회원_정보_수정_요청_시_실패() {
        MemberInfoUpdateRequestDto updateReq = new MemberInfoUpdateRequestDto("닉네임", "이미지");

        given().log().all()
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .put("/members/{id}", 1L)
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("다른 사용자의 정보를 수정하려고 하면 403 에러가 발생한다")
    void 타인_정보_수정_실패() {
        String attackerToken = signUpAndLogin("test@test.com", "Test111!", "공격자");
        Member victim = Member.builder()
                .email("victim@test.com")
                .password("Victim111!")
                .nickname("피해자")
                .isDeleted(false).build();
        memberRepository.save(victim);

        MemberInfoUpdateRequestDto updateReq = new MemberInfoUpdateRequestDto("공격시도", "공격시도이미지");

        given().log().all()
                .header("Authorization", "Bearer " + attackerToken)
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .put("/members/{id}", victim.getMemberId())
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("로그인 후 내 정보를 수정하면 DB에 반영된다")
    void 내_정보_수정_성공() {
        String accessToken = signUpAndLogin("test@test.com", "Test111!", "테스터");
        MemberInfoUpdateRequestDto updateReq = new MemberInfoUpdateRequestDto("테스트", "이미지");
        Long memberId = memberRepository.findByEmail("test@test.com").orElseThrow(() -> new RuntimeException("멤버 없음"))
                .getMemberId();

        given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .put("/members/{id}", memberId)
                .then().log().all()
                .statusCode(200);

        given().header("Authorization", "Bearer " + accessToken)
                .when().get("/members/me")
                .then()
                .body("data.nickname", equalTo("테스트"))
                .body("data.profileImage", equalTo("이미지"));
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 비밀번호 변경을 요청하면 401 에러가 발생한다")
    void 비로그인_비밀번호_변경_요청_시_실패() {
        PasswordUpdateRequestDto updateReq = new PasswordUpdateRequestDto("NewPw123!", "NewPw123!");

        given().log().all()
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .patch("/members/{id}", 1L)
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("다른 사용자의 비밀번호를 수정하려고 하면 403 에러가 발생한다")
    void 타인_비밀번호_변경_실패() {
        String attackerToken = signUpAndLogin("test@test.com", "Test111!", "공격자");
        Member victim = Member.builder()
                .email("victim@test.com")
                .password("Victim111!")
                .nickname("피해자")
                .isDeleted(false).build();
        memberRepository.save(victim);

        PasswordUpdateRequestDto updateReq = new PasswordUpdateRequestDto("Attack111!", "Attack111!");

        given().log().all()
                .header("Authorization", "Bearer " + attackerToken)
                .cookie("refreshToken", "dummy")
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .patch("/members/{id}", victim.getMemberId())
                .then().log().all()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("로그인 후 비밀번호를 변경하면 DB에 반영된다")
    void 비밀번호_변경_성공() {
        String accessToken = signUpAndLogin("test@test.com", "Test111!", "테스터");
        PasswordUpdateRequestDto updateReq = new PasswordUpdateRequestDto("Test123!", "Test123!");
        Long memberId = memberRepository.findByEmail("test@test.com").orElseThrow(() -> new RuntimeException("멤버 없음"))
                .getMemberId();

        given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .cookie("refreshToken", "dummy")
                .contentType(ContentType.JSON)
                .body(updateReq)
                .when()
                .patch("/members/{id}", memberId)
                .then().log().all()
                .statusCode(200);

        Member updatedMember = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("멤버 없음"));
        boolean isMatched = passwordEncoder.matches("Test123!", updatedMember.getPassword());

        assertTrue(isMatched);
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 회원 탈퇴를 요청하면 401 에러가 발생한다")
    void 비로그인_회원_탈퇴_요청_시_실패() {
        given().log().all()
                .when()
                .delete("/members/{id}", 1L)
                .then().log().all()
                .statusCode(401);
    }

    @Test
    @DisplayName("다른 사용자의 계정을 탈퇴하려고 하면 403 에러가 발생한다")
    void 타인_계정_탈퇴_실패() {
        String attackerToken = signUpAndLogin("test@test.com", "Test111!", "공격자");
        Member victim = Member.builder()
                .email("victim@test.com")
                .password("Victim111!")
                .nickname("피해자")
                .isDeleted(false).build();
        memberRepository.save(victim);

        given().log().all()
                .header("Authorization", "Bearer " + attackerToken)
                .cookie("refreshToken", "dummy")
                .when()
                .delete("/members/{id}", victim.getMemberId())
                .then().log().all()
                .statusCode(403);
    }

    @Test
    @DisplayName("회원 탈퇴 시 해당 계정으로 다시 로그인할 수 없다")
    void 회원_탈퇴_후_해당_계정으로_로그인_불가() {
        String accessToken = signUpAndLogin("test@test.com", "Test111!", "테스터");
        Long memberId = memberRepository.findByEmail("test@test.com").orElseThrow(() -> new RuntimeException("멤버 없음"))
                .getMemberId();

        given().header("Authorization", "Bearer " + accessToken)
                .cookie("refreshToken", "dummy")
                .when()
                .delete("/members/{id}", memberId)
                .then().statusCode(204);

        LoginRequestDto loginReq = new LoginRequestDto("test@test.com", "Test111!");
        given().contentType(ContentType.JSON)
                .body(loginReq)
                .when()
                .post("/members/session")
                .then().log().all()
                .statusCode(404);
    }
}
