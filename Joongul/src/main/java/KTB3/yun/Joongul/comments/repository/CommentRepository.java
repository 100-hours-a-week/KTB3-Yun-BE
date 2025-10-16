package KTB3.yun.Joongul.comments.repository;

import KTB3.yun.Joongul.comments.dto.CommentResponseDto;
import KTB3.yun.Joongul.comments.dto.CommentUpdateRequestDto;
import KTB3.yun.Joongul.comments.dto.CommentWriteRequestDto;

import java.util.List;

public interface CommentRepository {
    List<CommentResponseDto> getAllComments(Long postId);
    CommentResponseDto writeComment(Long postId, CommentWriteRequestDto commentRequestDto, Long memberId);
    CommentResponseDto updateComment(Long postId, Long commentId, CommentUpdateRequestDto commentRequestDto, Long memberId);
    void deleteComment(Long commentId, Long memberId);
    Long findMemberIdByCommentId(Long commentId);
}
