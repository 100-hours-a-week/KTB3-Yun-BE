package KTB3.yun.Joongul.comments.repository;

import KTB3.yun.Joongul.comments.domain.Comment;
import KTB3.yun.Joongul.comments.domain.CommentData;
import KTB3.yun.Joongul.comments.dto.CommentResponseDto;
import KTB3.yun.Joongul.comments.dto.CommentUpdateRequestDto;
import KTB3.yun.Joongul.comments.dto.CommentWriteRequestDto;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.posts.domain.Post;
import KTB3.yun.Joongul.posts.domain.PostData;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static KTB3.yun.Joongul.comments.domain.CommentData.commentSequence;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final MemberRepository memberRepository;

    public CommentRepositoryImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<CommentResponseDto> getAllComments(Long postId) {
        Optional<Post> post = Optional.ofNullable(PostData.POSTS.get(postId));
        if (post.isEmpty()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, "존재하지 않는 리소스입니다.");
        }
        List<Long> commentIds = CommentData.COMMENT_IDS_IN_POST.get(postId);

        return commentIds.stream()
                .map(CommentData.COMMENTS::get)
                .filter(Objects::nonNull)
                .map(CommentResponseDto::from)
                .toList();
    }

    @Override
    synchronized public CommentResponseDto writeComment(Long postId, CommentWriteRequestDto dto, Long memberId) {
        Optional<Post> post = Optional.ofNullable(PostData.POSTS.get(postId));
        if (post.isEmpty()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, "존재하지 않는 리소스입니다.");
        }
        CommentData.COMMENTS.put(commentSequence, new Comment(memberId, postId,
                commentSequence, memberRepository.getMemberInfo(memberId).getNickname(), dto.getContent(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        CommentData.COMMENT_IDS_IN_POST.get(postId).add(commentSequence);
        Comment comment = CommentData.COMMENTS.get(commentSequence);
        commentSequence++;
        return CommentResponseDto.from(comment);
    }

    @Override
    public CommentResponseDto updateComment(Long postId, Long commentId, CommentUpdateRequestDto dto, Long memberId) {
        Optional<Post> post = Optional.ofNullable(PostData.POSTS.get(postId));
        if (post.isEmpty()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, "존재하지 않는 리소스입니다.");
        }
        Comment oldComment = CommentData.COMMENTS.get(commentId);
        CommentData.COMMENTS.put(commentId, new Comment(memberId, oldComment.getPostId(),
                commentId, oldComment.getNickname(), dto.getContent(), oldComment.getCreatedAt()));
        Comment updatedComment = CommentData.COMMENTS.get(commentId);
        return CommentResponseDto.from(updatedComment);
    }

    @Override
    public void deleteComment(Long commentId, Long postId) {
        Optional<Post> post = Optional.ofNullable(PostData.POSTS.get(postId));
        if (post.isEmpty()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, "존재하지 않는 리소스입니다.");
        }
        if (CommentData.COMMENTS.get(commentId) == null) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, "존재하지 않는 리소스입니다.");
        }
        CommentData.COMMENTS.remove(commentId);
        CommentData.COMMENT_IDS_IN_POST.get(postId).remove(commentId);
    }

    @Override
    public Long findMemberIdByCommentId(Long commentId) {
        return CommentData.COMMENTS.get(commentId).getMemberId();
    }
}
