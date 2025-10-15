package KTB3.yun.Joongul.members.repository;

import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.MemberData;
import KTB3.yun.Joongul.members.dto.MemberInfoUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.PasswordUpdateRequestDto;
import KTB3.yun.Joongul.members.dto.SignupRequestDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import static KTB3.yun.Joongul.members.domain.MemberData.sequence;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final BCryptPasswordEncoder passwordEncoder;

    public MemberRepositoryImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    synchronized public void addMember(SignupRequestDto dto) {
        MemberData.MEMBERS.put(sequence, new Member(sequence, dto.getEmail(), passwordEncoder.encode(dto.getPassword()),
                dto.getNickname(), dto.getProfileImage()));
        MemberData.EMAILS.put(dto.getEmail(), sequence);
        MemberData.NICKNAMES.put(dto.getNickname(), sequence);
        sequence++;
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
    public void modifyPassword(PasswordUpdateRequestDto dto, Long memberId){
        Member member = MemberData.MEMBERS.get(memberId);
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
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

    @Override
    public boolean alreadyUsingPassword(Long memberId, String password) {
        return passwordEncoder.matches(password, MemberData.MEMBERS.get(memberId).getPassword());
    }

    @Override
    public boolean isCorrectPassword(Long memberId, String password) {
        return passwordEncoder.matches(password, MemberData.MEMBERS.get(memberId).getPassword());
    }
}
