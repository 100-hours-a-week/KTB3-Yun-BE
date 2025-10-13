package KTB3.yun.Joongul.members.repository;

import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.dto.MemberInfoUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.PasswordUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.SignupRequestDto;

public interface MemberRepository {
    void addMember(SignupRequestDto signupRequestDto);
    Member getMemberInfo(Long memberId);
    void updateMemberInfo(MemberInfoUpdateRequestDto memberInfoUpdateRequestDto);
    void modifyPassword(PasswordUpdateRequestDto passwordUpdateRequestDto);
    void deleteMember(Long memberId);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean alreadyUsingPassword(Long memberId, String password);
}
