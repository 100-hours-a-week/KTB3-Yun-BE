package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.MemberData;
import KTB3.yun.Joongul.members.dto.*;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static KTB3.yun.Joongul.members.domain.MemberData.memberSequence;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //이런 식으로 비즈니스 로직 검사에 변수를 활용하면 좀 더 가독성이 좋아지지 않을까 생각했습니다.
    private boolean isExistEmail;
    private boolean isExistNickname;
    private boolean isUsedPassword;
    private boolean isSameWithConfirmPassword;
    private boolean isCorrectEmail;
    private boolean isCorrectPassword;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(SignUpRequestDto signupRequestDto) {
        isExistEmail = memberRepository.existsByEmail(signupRequestDto.getEmail());
        isExistNickname = memberRepository.existsByNickname(signupRequestDto.getNickname());
        isSameWithConfirmPassword = signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword());

        if (isExistEmail) {
            throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL, "중복된 이메일입니다.");
        } else if (isExistNickname) {
            throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, "중복된 닉네임입니다.");
        } else if (!isSameWithConfirmPassword) {
            throw new ApplicationException(ErrorCode.NOT_SAME_WITH_CONFIRM, "비밀번호가 다릅니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        Member member = new Member(memberSequence, signupRequestDto.getEmail(), encodedPassword,
                signupRequestDto.getNickname(), signupRequestDto.getProfileImage());
        memberRepository.addMember(member);
    }

    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.getMemberInfo(memberId);
        if (member == null) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, "존재하지 않는 회원입니다.");
        }
        return new MemberInfoResponseDto(member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage());
    }

    public void updateMemberInfo(MemberInfoUpdateRequestDto memberInfoUpdateRequestDto, Long memberId) {
        isExistNickname = memberRepository.existsByNickname(memberInfoUpdateRequestDto.getNickname());

        if (isExistNickname) {
            throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, "이미 사용 중인 닉네임입니다.");
        }

        memberRepository.updateMemberInfo(memberInfoUpdateRequestDto, memberId);
    }

    public void modifyPassword(PasswordUpdateRequestDto passwordUpdateRequestDto, Long memberId) {
        isUsedPassword = alreadyUsingPassword(memberId, passwordUpdateRequestDto.getPassword());
        isSameWithConfirmPassword = passwordUpdateRequestDto.getPassword().equals(passwordUpdateRequestDto.getConfirmPassword());

        if (isUsedPassword) {
            throw new ApplicationException(ErrorCode.USING_PASSWORD, "이미 사용 중인 비밀번호입니다.");
        } else if (!isSameWithConfirmPassword) {
            throw new ApplicationException(ErrorCode.NOT_SAME_WITH_CONFIRM, "비밀번호가 다릅니다.");
        }

        String newEncodedPassword = passwordEncoder.encode(passwordUpdateRequestDto.getPassword());
        memberRepository.modifyPassword(newEncodedPassword, memberId);
    }

    public void withdraw(Long memberId) {
        memberRepository.deleteMember(memberId);
    }

    //일단 Service 쪽에서 이메일/비밀번호 검증을 해서 틀리면 예외를 던지게끔 했는데 이 구조가 맞는지 모르겠습니다...
    public boolean isCorrectMember(LoginRequestDto loginRequestDto) {
        Long memberId = memberRepository.findIdByEmail(loginRequestDto.getEmail());
        isCorrectEmail = memberRepository.getMemberInfo(memberId).getEmail().equals(loginRequestDto.getEmail());
        isCorrectPassword = isCorrectPassword(memberId, loginRequestDto.getPassword());

        if (!isCorrectEmail || !isCorrectPassword) {
            throw new ApplicationException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, "이메일 또는 비밀번호가 다릅니다.");
        }

        return true;
    }

    public Long findIdByEmail(String email) {
        return memberRepository.findIdByEmail(email);
    }

    public boolean alreadyUsingPassword(Long memberId, String password) {
        return passwordEncoder.matches(password, MemberData.MEMBERS.get(memberId).getPassword());
    }

    public boolean isCorrectPassword(Long memberId, String password) {
        return passwordEncoder.matches(password, MemberData.MEMBERS.get(memberId).getPassword());
    }
}
