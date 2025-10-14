package KTB3.yun.Joongul.members.controller;

import KTB3.yun.Joongul.common.dto.ApiResponse;
import KTB3.yun.Joongul.members.dto.*;
import KTB3.yun.Joongul.members.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<String> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("register_success");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MemberInfoResponseDto>> showMemberInfo(@RequestParam(name = "memberId") Long memberId) {
        MemberInfoResponseDto info = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok().body(new ApiResponse<>("get_user_info_success", info));
    }

    @PutMapping
    public ResponseEntity<String> updateMemberInfo(@RequestBody @Valid MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {
        memberService.updateMemberInfo(memberInfoUpdateRequestDto);
        return ResponseEntity.ok().body("user_info_change_success");
    }

    @PatchMapping
    public ResponseEntity<String> modifyPassword(@RequestBody @Valid PasswordUpdateRequestDto passwordUpdateRequestDto) {
        memberService.modifyPassword(passwordUpdateRequestDto);
        return ResponseEntity.ok().body("password_change_success");
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@RequestParam(name = "memberId") Long memberId) {
        memberService.withdraw(memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/session")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequestDto,
                                                  HttpServletRequest request) {
        if (memberService.isCorrectMember(loginRequestDto)) {
            HttpSession session = request.getSession();
            session.setAttribute("USER_ID", loginRequestDto.getEmail());
        }
        return ResponseEntity.ok("login_success");
    }

    //로그아웃의 HTTP Method를 POST로 했는데, 그래서 그런지 RESTful한 이름이 떠오르지 않습니다..
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
