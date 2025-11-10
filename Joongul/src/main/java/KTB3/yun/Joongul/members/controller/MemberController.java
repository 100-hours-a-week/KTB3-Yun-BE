package KTB3.yun.Joongul.members.controller;

import KTB3.yun.Joongul.common.auth.AuthService;
import KTB3.yun.Joongul.common.dto.ApiResponseDto;
import KTB3.yun.Joongul.members.dto.*;
import KTB3.yun.Joongul.members.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member-Controller", description = "Member CRUD API")
@RestController
@RequestMapping("/members")
@CrossOrigin(origins = "http://127.0.0.1:5500/", allowCredentials = "true")
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

    @Operation(summary = "회원 정보 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<MemberInfoResponseDto>> getMemberInfo(@PathVariable(name = "id") Long memberId,
                                                                               HttpServletRequest request) {

        authService.checkLoginUser(request);
        authService.checkAuthority(request, memberId);
        MemberInfoResponseDto info = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok().body(new ApiResponseDto<>("get_user_info_success", info));
    }

    @Operation(summary = "회원 정보 수정 API")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMemberInfo(@PathVariable(name = "id") Long memberId,
                                                   @RequestBody @Valid MemberInfoUpdateRequestDto memberInfoUpdateRequestDto,
                                                   HttpServletRequest request) {
        authService.checkLoginUser(request);
        authService.checkAuthority(request, memberId);
        memberService.updateMemberInfo(memberInfoUpdateRequestDto, memberId);
        return ResponseEntity.ok().body("user_info_change_success");
    }

    @Operation(summary = "비밀번호 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity<String> modifyPassword(@PathVariable(name = "id") Long memberId,
                                                 @RequestBody @Valid PasswordUpdateRequestDto passwordUpdateRequestDto,
                                                 HttpServletRequest request) {
        authService.checkLoginUser(request);
        authService.checkAuthority(request, memberId);
        memberService.modifyPassword(passwordUpdateRequestDto, memberId);

        HttpSession session = request.getSession(false);
        session.invalidate();

        return ResponseEntity.ok().body("password_change_success");
    }

    @Operation(summary = "회원 탈퇴 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawMember(@PathVariable(name = "id") Long memberId,
                                               HttpServletRequest request) {
        authService.checkLoginUser(request);
        authService.checkAuthority(request, memberId);
        memberService.withdraw(memberId);

        HttpSession session = request.getSession(false);
        session.invalidate();

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "로그인 API")
    @PostMapping("/session")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto,
                                                  HttpServletRequest request) {
        if (memberService.isCorrectMember(loginRequestDto)) {
            HttpSession session = request.getSession();
            session.setAttribute("USER_ID", memberService.findIdByEmail(loginRequestDto.getEmail()));
        }
        return ResponseEntity.ok("login_success");
    }

    @Operation(summary = "로그아웃 API")
    //로그아웃의 HTTP Method를 POST로 했는데, 그래서 그런지 RESTful한 이름이 떠오르지 않습니다..
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
