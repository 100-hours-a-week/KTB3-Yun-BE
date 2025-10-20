package KTB3.yun.Joongul.members.repository;

import KTB3.yun.Joongul.members.domain.Member;

public interface MemberRepository {

    //Dto가 아닌 Member 객체를 받게 변경
    void addMember(Member member);
    Member getMemberInfo(Long memberId);
    void updateMemberInfo(String nickname, String profileImage, Long memberId);
    void modifyPassword(String newPassword, Long memberId);
    void deleteMember(Long memberId);
    Long findIdByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
