package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.dto.*;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL, ErrorCode.DUPLICATE_EMAIL.getMessage());
        } else if (isExistNickname) {
            throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, ErrorCode.DUPLICATE_NICKNAME.getMessage());
        } else if (!isSameWithConfirmPassword) {
            throw new ApplicationException(ErrorCode.NOT_SAME_WITH_CONFIRM, ErrorCode.NOT_SAME_WITH_CONFIRM.getMessage());
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        Member member = Member.builder()
                .email(signupRequestDto.getEmail())
                .password(encodedPassword)
                .nickname(signupRequestDto.getNickname())
                .profileImage(signupRequestDto.getProfileImage())
                .isDeleted(Boolean.FALSE)
                .build();
        memberRepository.save(member);
    }

    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        if (member.getIsDeleted()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }

        return new MemberInfoResponseDto(member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage());
    }

    @Transactional
    public void updateMemberInfo(MemberInfoUpdateRequestDto dto, Long memberId) {
        isExistNickname = memberRepository.existsByNickname(dto.getNickname());

        if (isExistNickname) {
            throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, ErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        member.updateMemberInfo(dto.getNickname(), dto.getProfileImage());
    }

    @Transactional
    public void modifyPassword(PasswordUpdateRequestDto passwordUpdateRequestDto, Long memberId) {
        isUsedPassword = isValidPassword(memberId, passwordUpdateRequestDto.getPassword());
        isSameWithConfirmPassword = passwordUpdateRequestDto.getPassword().equals(passwordUpdateRequestDto.getConfirmPassword());

        if (isUsedPassword) {
            throw new ApplicationException(ErrorCode.USING_PASSWORD, ErrorCode.USING_PASSWORD.getMessage());
        } else if (!isSameWithConfirmPassword) {
            throw new ApplicationException(ErrorCode.NOT_SAME_WITH_CONFIRM, ErrorCode.NOT_SAME_WITH_CONFIRM.getMessage());
        }

        String newEncodedPassword = passwordEncoder.encode(passwordUpdateRequestDto.getPassword());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        member.modifyPassword(newEncodedPassword);
    }

    @Transactional
    public void withdraw(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage())).deleteMember();
    }

    //일단 Service 쪽에서 이메일/비밀번호 검증을 해서 틀리면 예외를 던지게끔 했는데 이 구조가 맞는지 모르겠습니다...
    public boolean isCorrectMember(LoginRequestDto loginRequestDto) {
        Long memberId = findIdByEmail(loginRequestDto.getEmail());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        if (Boolean.TRUE.equals(member.getIsDeleted())) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }

        isCorrectEmail = member.getEmail().equals(loginRequestDto.getEmail());
        isCorrectPassword = isValidPassword(memberId, loginRequestDto.getPassword());

        if (!isCorrectEmail || !isCorrectPassword) {
            throw new ApplicationException(ErrorCode.INVALID_EMAIL_OR_PASSWORD,
                    ErrorCode.INVALID_EMAIL_OR_PASSWORD.getMessage());
        }

        return true;
    }

    public Long findIdByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()))
                .getMemberId();
    }

    public boolean isValidPassword(Long memberId, String password) {
        String savedPassword = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()))
                .getPassword();
        return passwordEncoder.matches(password, savedPassword);
    }
}
