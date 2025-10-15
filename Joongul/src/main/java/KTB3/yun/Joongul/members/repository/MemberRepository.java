package KTB3.yun.Joongul.members.repository;

import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.dto.MemberInfoUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.PasswordUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.SignupRequestDto;

public interface MemberRepository {
    void addMember(SignupRequestDto signupRequestDto);
    Member getMemberInfo(Long memberId);
    void updateMemberInfo(MemberInfoUpdateRequestDto memberInfoUpdateRequestDto, Long memberId);
    void modifyPassword(PasswordUpdateRequestDto passwordUpdateRequestDto, Long memberId);
    void deleteMember(Long memberId);
    Long findIdByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean alreadyUsingPassword(Long memberId, String password);
    boolean isCorrectPassword(Long memberId, String password);
}
