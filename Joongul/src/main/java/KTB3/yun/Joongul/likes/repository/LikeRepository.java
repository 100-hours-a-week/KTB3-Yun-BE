package KTB3.yun.Joongul.likes.repository;

import KTB3.yun.Joongul.likes.domain.Like;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
//    void toggleLike(Long postId, Long memberId);
//    void untoggleLike(Long postId, Long memberId);


    @Override
    @Nonnull
    Optional<Like> findById(@Nonnull Long id);

    @Override
    @Nonnull
    <S extends Like> S save(@Nonnull S entity);

    @Override
    void delete(@Nonnull Like entity);

    Optional<Like> findByPost_PostIdAndMember_MemberId(Long postId, Long memberId);
}
