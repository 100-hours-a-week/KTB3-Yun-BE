package KTB3.yun.Joongul.members.controller;

import KTB3.yun.Joongul.common.auth.AuthService;
import KTB3.yun.Joongul.common.dto.ApiResponseDto;
import KTB3.yun.Joongul.members.dto.*;
import KTB3.yun.Joongul.members.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member-Controller", description = "Member CRUD API")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    public MemberController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }


    @Operation(summary = "회원 가입 API")
    @PostMapping
    public ResponseEntity<String> signup(@RequestBody @Valid SignUpRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("register_success");
    }

    @Operation(summary = "로그인 회원 정보 조회 API")
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDto<MemberInfoResponseDto>> getMemberInfo(HttpServletRequest request) {

        Long memberId = authService.getMemberId(request);
        MemberInfoResponseDto info = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok().body(new ApiResponseDto<>("get_user_info_success", info));
    }

    @Operation(summary = "회원 정보 수정 API")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMemberInfo(@PathVariable(name = "id") Long memberId,
                                                   @RequestBody @Valid MemberInfoUpdateRequestDto memberInfoUpdateRequestDto,
                                                   HttpServletRequest request) {
        authService.checkAuthority(request, memberId);
        memberService.updateMemberInfo(memberInfoUpdateRequestDto, memberId);
        return ResponseEntity.ok().body("user_info_change_success");
    }

    @Operation(summary = "비밀번호 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity<String> modifyPassword(@PathVariable(name = "id") Long memberId,
                                                 @RequestBody @Valid PasswordUpdateRequestDto passwordUpdateRequestDto,
                                                 HttpServletRequest request) {
        authService.checkAuthority(request, memberId);
        memberService.modifyPassword(passwordUpdateRequestDto, memberId);

        return ResponseEntity.ok().body("password_change_success");
    }

    @Operation(summary = "회원 탈퇴 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawMember(@PathVariable(name = "id") Long memberId,
                                               HttpServletRequest request,
                                               HttpServletResponse response) {
        authService.checkAuthority(request, memberId);
        memberService.withdraw(memberId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/session")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto,
                                                  HttpServletResponse response) {
        memberService.isCorrectMember(loginRequestDto);
        LoginResponseDto loginResponseDto = memberService.login(loginRequestDto);
        String refreshToken = loginResponseDto.getRefreshToken();
        String setToken = "refreshToken=" + refreshToken + "; HttpOnly; " + "SameSite=Lax; "
                + "Path=/; " + "Max-Age=" + loginResponseDto.getRefreshTokenExpireTime();

        response.addHeader("Set-Cookie", setToken);
        return ResponseEntity.ok(loginResponseDto);
    }

    @Operation(summary = "로그아웃 API")
    //로그아웃의 HTTP Method를 POST로 했는데, 그래서 그런지 RESTful한 이름이 떠오르지 않습니다..
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Set-Cookie", "refreshToken=; Max-Age=0; Path=/; HttpOnly");
        return ResponseEntity.noContent().build();
    }
}
