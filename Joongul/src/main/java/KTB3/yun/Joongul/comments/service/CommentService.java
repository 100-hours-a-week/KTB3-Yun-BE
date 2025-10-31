package KTB3.yun.Joongul.comments.service;

import KTB3.yun.Joongul.comments.domain.Comment;
import KTB3.yun.Joongul.comments.dto.CommentResponseDto;
import KTB3.yun.Joongul.comments.dto.CommentUpdateRequestDto;
import KTB3.yun.Joongul.comments.dto.CommentWriteRequestDto;
import KTB3.yun.Joongul.comments.repository.CommentRepository;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.posts.domain.Post;
import KTB3.yun.Joongul.posts.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    /*
    댓글을 작성, 삭제했을 때 Post의 comments 필드의 수를 변경해줘야 하는데 그러면 CommentService <-> PostService 순환참조 문제 발생
    그래서 일단은 static에 직접 접근해서 값을 변경하는 것으로 구현했습니다... 올바른 구조 같지는 않아 보입니다.
    */
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, MemberRepository memberRepository,
                          PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository.findAllByPost_PostId(postId)
                .stream()
                .filter(comment -> !comment.getIsDeleted())
                .map(CommentResponseDto::from)
                .toList();
    }

    @Transactional
    public CommentResponseDto writeComment(Long postId, CommentWriteRequestDto dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        Comment comment = Comment.builder()
                .nickname(member.getNickname())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .isDeleted(Boolean.FALSE)
                .member(member)
                .post(post)
                .build();

        commentRepository.save(comment);
        post.addComment(comment);
        post.increaseComments();

        return CommentResponseDto.from(comment);
    }

    @Transactional
    public CommentResponseDto updateComment(Long commentId, Long postId, CommentUpdateRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        comment.updateComment(dto.getContent());
        return CommentResponseDto.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long postId) {
        commentRepository.deleteById(commentId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));
        post.decreaseComments();
    }

    public Long getMemberId(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()))
                .getMember().getMemberId();
    }
}
