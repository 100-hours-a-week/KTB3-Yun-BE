package KTB3.yun.Joongul.comments.service;

import KTB3.yun.Joongul.comments.domain.CommentData;
import KTB3.yun.Joongul.comments.dto.CommentResponseDto;
import KTB3.yun.Joongul.comments.dto.CommentUpdateRequestDto;
import KTB3.yun.Joongul.comments.dto.CommentWriteRequestDto;
import KTB3.yun.Joongul.comments.repository.CommentRepository;
import KTB3.yun.Joongul.posts.domain.PostData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    /*
    댓글을 작성, 삭제했을 때 Post의 comments 필드의 수를 변경해줘야 하는데 그러면 CommentService <-> PostService 순환참조 문제 발생
    그래서 일단은 static에 직접 접근해서 값을 변경하는 것으로 구현했습니다... 올바른 구조 같지는 않아 보입니다.
    */
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository.getAllComments(postId);
    }

    public CommentResponseDto writeComment(Long postId, CommentWriteRequestDto dto, Long memberId) {
        CommentResponseDto comment = commentRepository.writeComment(postId, dto, memberId);
        PostData.POSTS.get(postId).setComments(CommentData.COMMENT_IDS_IN_POST.get(postId).size());
        return comment;
    }

    public CommentResponseDto updateComment(Long postId, Long commentId, CommentUpdateRequestDto dto, Long memberId) {
        return commentRepository.updateComment(postId, commentId, dto, memberId);
    }

    public void deleteComment(Long commentId, Long postId) {
        commentRepository.deleteComment(commentId, postId);
        PostData.POSTS.get(postId).setComments(CommentData.COMMENT_IDS_IN_POST.get(postId).size());
    }

    public Long getMemberId(Long commentId) {
        return commentRepository.findMemberIdByCommentId(commentId);
    }
}
