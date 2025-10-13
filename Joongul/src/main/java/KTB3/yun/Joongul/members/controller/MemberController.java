package KTB3.yun.Joongul.members.controller;

import KTB3.yun.Joongul.common.api.ApiResponse;
import KTB3.yun.Joongul.members.dto.MemberInfoResponseDto;
import KTB3.yun.Joongul.members.dto.MemberInfoUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.PasswordUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.SignupRequestDto;
import KTB3.yun.Joongul.members.service.MemberService;
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
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto signupRequestDto) {
        memberService.signup(signupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("register_success");
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MemberInfoResponseDto>> showMemberInfo(@RequestParam(name = "memberId") Long memberId) {
        MemberInfoResponseDto info = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok().body(new ApiResponse<>("get_user_info_success", info));
    }

    @PutMapping
    public ResponseEntity<String> updateMemberInfo(@RequestBody MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {
        memberService.updateMemberInfo(memberInfoUpdateRequestDto);
        return ResponseEntity.ok().body("user_info_change_success");
    }

    @PatchMapping
    public ResponseEntity<String> modifyPassword(@RequestBody PasswordUpdateRequestDto passwordUpdateRequestDto) {
        memberService.modifyPassword(passwordUpdateRequestDto);
        return ResponseEntity.ok().body("password_change_success");
    }

    @DeleteMapping
    public void withdraw(@RequestParam(name = "memberId") Long memberId) {
        memberService.withdraw(memberId);
    }
}
