package KTB3.yun.Joongul.posts.repository;

import KTB3.yun.Joongul.posts.domain.Post;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
//    List<PostSimpleResponseDto> findAll();
//    PostDetailResponseDto findById(Long postId);
//    PostDetailResponseDto save(PostWriteRequestDto postWriteRequestDto, Long memberId);
//    PostDetailResponseDto update(Long postId, PostUpdateRequestDto postUpdateRequestDto, Long memberId);
//    void delete(Long postId);
//
//    Long findMemberIdByPostId(Long postId);

    @Override
    @Nonnull
    List<Post> findAll();

    @Override
    @Nonnull
    Optional<Post> findById(@Nonnull Long id);

    @Override
    @Nonnull
    <S extends Post> S save(@Nonnull S s);

    @Override
    void deleteById(@Nonnull Long id);
}
