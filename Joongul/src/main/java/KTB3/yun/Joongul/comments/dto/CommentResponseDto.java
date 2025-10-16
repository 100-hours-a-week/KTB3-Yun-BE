package KTB3.yun.Joongul.comments.dto;

import KTB3.yun.Joongul.comments.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String nickname;
    private String content;
    private String createdAt;

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(comment.getCommentId(), comment.getNickname(),
                comment.getContent(), comment.getCreatedAt());
    }
}
