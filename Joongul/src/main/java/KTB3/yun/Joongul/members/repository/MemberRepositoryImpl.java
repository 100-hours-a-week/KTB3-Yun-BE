package KTB3.yun.Joongul.members.repository;

import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.MemberData;
import KTB3.yun.Joongul.members.dto.MemberInfoUpdateRequestDto;
import org.springframework.stereotype.Repository;

import static KTB3.yun.Joongul.members.domain.MemberData.memberSequence;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    @Override
    //memberSequence 변수의 메모리 가시성과 원자성 확보를 위해 synchronized를 적용했습니다.
    synchronized public void addMember(Member member) {
        MemberData.MEMBERS.put(memberSequence, member);
        MemberData.EMAILS.put(member.getEmail(), memberSequence);
        MemberData.NICKNAMES.put(member.getNickname(), memberSequence);
        memberSequence++;
    }

    @Override
    public Member getMemberInfo(Long memberId) {
        return MemberData.MEMBERS.get(memberId);
    }

    @Override
    public void updateMemberInfo(MemberInfoUpdateRequestDto dto, Long memberId){
        Member member = MemberData.MEMBERS.get(memberId);

        String memberNickname = member.getNickname();
        MemberData.NICKNAMES.remove(memberNickname);
        MemberData.NICKNAMES.put(dto.getNickname(), memberId);

        member.setNickname(dto.getNickname());
        member.setProfileImage(dto.getProfileImage());
    }

    @Override
    public void modifyPassword(String newPassword, Long memberId){
        Member member = MemberData.MEMBERS.get(memberId);
        member.setPassword(newPassword);
    }

    @Override
    public void deleteMember(Long memberId) {
        MemberData.EMAILS.remove(MemberData.MEMBERS.get(memberId).getEmail());
        MemberData.NICKNAMES.remove(MemberData.MEMBERS.get(memberId).getNickname());
        MemberData.MEMBERS.remove(memberId);
    }

    @Override
    public Long findIdByEmail(String email) {
        return MemberData.EMAILS.get(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return MemberData.EMAILS.containsKey(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return MemberData.NICKNAMES.containsKey(nickname);
    }

    /*

    비밀번호 인코딩을 Repository 계층에서 하고 있음 -> SRP 위반

    @Override
    public boolean alreadyUsingPassword(Long memberId, String password) {
        return passwordEncoder.matches(password, MemberData.MEMBERS.get(memberId).getPassword());
    }

    @Override
    public boolean isCorrectPassword(Long memberId, String password) {
        return passwordEncoder.matches(password, MemberData.MEMBERS.get(memberId).getPassword());
    }
     */
}
