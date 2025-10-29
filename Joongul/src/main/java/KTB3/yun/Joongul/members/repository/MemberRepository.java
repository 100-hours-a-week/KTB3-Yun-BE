package KTB3.yun.Joongul.members.repository;

import KTB3.yun.Joongul.members.domain.Member;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

//    //Dto가 아닌 Member 객체를 받게 변경
//    void addMember(Member member);
//    Member getMemberInfo(Long memberId);
//    void updateMemberInfo(String nickname, String profileImage, Long memberId);
//    void modifyPassword(String newPassword, Long memberId);
//    void deleteMember(Long memberId);
//    Long findIdByEmail(String email);
//    boolean existsByEmail(String email);
//    boolean existsByNickname(String nickname);

    @Override
    @Nonnull
    <S extends Member> S save(@Nonnull S entity);

    @Override
    @Nonnull
    Optional<Member> findById(@Nonnull Long id);

    @Override
    void deleteById(@Nonnull Long id);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}