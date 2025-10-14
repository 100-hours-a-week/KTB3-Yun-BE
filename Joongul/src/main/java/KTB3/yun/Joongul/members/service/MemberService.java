package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.dto.MemberInfoResponseDto;
import KTB3.yun.Joongul.members.dto.MemberInfoUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.PasswordUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.SignupRequestDto;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    //이런 식으로 비즈니스 로직 검사에 변수를 활용하면 좀 더 가독성이 좋아지지 않을까 생각했습니다.
    private boolean isExistEmail;
    private boolean isExistNickname;
    private boolean isUsedPassword;
    private boolean isSamePassword;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void signup(SignupRequestDto signupRequestDto) {
        isExistEmail = memberRepository.existsByEmail(signupRequestDto.getEmail());
        isExistNickname = memberRepository.existsByNickname(signupRequestDto.getNickname());
        isSamePassword = signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword());

        if (isExistEmail) {
            throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL, "중복된 이메일입니다.");
        } else if (isExistNickname) {
            throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, "중복된 닉네임입니다.");
        } else if (!isSamePassword) {
            throw new ApplicationException(ErrorCode.NOT_SAME_PASSWORD, "비밀번호가 다릅니다.");
        }

        memberRepository.addMember(signupRequestDto);
    }

    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.getMemberInfo(memberId);
        return new MemberInfoResponseDto(member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage());
    }

    public void updateMemberInfo(MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {
        isExistNickname = memberRepository.existsByNickname(memberInfoUpdateRequestDto.getNickname());

        if (isExistNickname) {
            throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다.");
        }

        memberRepository.updateMemberInfo(memberInfoUpdateRequestDto);
    }

    public void modifyPassword(PasswordUpdateRequestDto passwordUpdateRequestDto) {
        isUsedPassword = memberRepository.alreadyUsingPassword(passwordUpdateRequestDto.getMemberId(),
                passwordUpdateRequestDto.getPassword());
        isSamePassword = passwordUpdateRequestDto.getPassword().equals(passwordUpdateRequestDto.getConfirmPassword());

        if (isUsedPassword) {
            throw new ApplicationException(ErrorCode.USING_PASSWORD, "이미 사용 중인 비밀번호입니다.");
        } else if (!isSamePassword) {
            throw new ApplicationException(ErrorCode.NOT_SAME_PASSWORD, "비밀번호가 다릅니다.");
        }

        memberRepository.modifyPassword(passwordUpdateRequestDto);
    }

    public void withdraw(Long memberId) {
        memberRepository.deleteMember(memberId);
    }
}
