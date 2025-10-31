package KTB3.yun.Joongul.comments.repository;

import KTB3.yun.Joongul.comments.domain.Comment;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
//    List<CommentResponseDto> getAllComments(Long postId);
//    CommentResponseDto writeComment(Long postId, CommentWriteRequestDto commentRequestDto, Long memberId);
//    CommentResponseDto updateComment(Long postId, Long commentId, CommentUpdateRequestDto commentRequestDto, Long memberId);
//    void deleteComment(Long commentId, Long memberId);
//    Long findMemberIdByCommentId(Long commentId);

    List<Comment> findAllByPost_PostId(Long postId);

    @Override
    @Nonnull
    Optional<Comment> findById(@Nonnull Long commentId);
    Comment findByCommentIdAndPost_PostId(Long commentId, Long postId);

    @Override
    @Nonnull
    <S extends Comment> S save(@Nonnull S s);

    @Override
    void deleteById(@Nonnull Long id);
}
