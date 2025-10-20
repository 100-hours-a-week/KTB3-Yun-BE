package KTB3.yun.Joongul.members.controller;

import KTB3.yun.Joongul.common.dto.ApiResponseDto;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
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
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원 가입 API")
    @PostMapping
    public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("register_success");
    }

    @Operation(summary = "회원 정보 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<MemberInfoResponseDto>> getMemberInfo(@PathVariable(name = "id") Long memberId,
                                                                               HttpServletRequest request) {

        //401, 403 예외 처리하는 부분이 계속 반복되는데 어떻게 분리하면 좋을지 고민입니다.
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }

        Long loginId = (Long) session.getAttribute("USER_ID");
        if (!loginId.equals(memberId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, "잘못된 접근입니다.");
        }
        MemberInfoResponseDto info = memberService.getMemberInfo(loginId);
        return ResponseEntity.ok().body(new ApiResponseDto<>("get_user_info_success", info));
    }

    @Operation(summary = "회원 정보 수정 API")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateMemberInfo(@PathVariable(name = "id") Long memberId,
                                                   @RequestBody @Valid MemberInfoUpdateRequestDto memberInfoUpdateRequestDto,
                                                   HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }

        Long loginId = (Long) session.getAttribute("USER_ID");
        if (!loginId.equals(memberId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, "잘못된 접근입니다.");
        }
        memberService.updateMemberInfo(memberInfoUpdateRequestDto, loginId);
        return ResponseEntity.ok().body("user_info_change_success");
    }

    @Operation(summary = "비밀번호 수정 API")
    @PatchMapping("/{id}")
    public ResponseEntity<String> modifyPassword(@PathVariable(name = "id") Long memberId,
                                                 @RequestBody @Valid PasswordUpdateRequestDto passwordUpdateRequestDto,
                                                 HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }

        Long loginId = (Long) session.getAttribute("USER_ID");
        if (!loginId.equals(memberId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, "잘못된 접근입니다.");
        }
        memberService.modifyPassword(passwordUpdateRequestDto, loginId);
        return ResponseEntity.ok().body("password_change_success");
    }

    @Operation(summary = "회원 탈퇴 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawMember(@PathVariable(name = "id") Long memberId,
                                               HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }

        Long loginId = (Long) session.getAttribute("USER_ID");
        if (!loginId.equals(memberId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, "잘못된 접근입니다.");
        }
        memberService.withdraw(loginId);
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
