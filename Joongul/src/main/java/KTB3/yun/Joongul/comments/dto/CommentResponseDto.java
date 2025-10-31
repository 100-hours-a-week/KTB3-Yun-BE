package KTB3.yun.Joongul.comments.dto;

import KTB3.yun.Joongul.comments.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private String nickname;
    private String content;
    private String createdAt;

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(comment.getCommentId(), comment.getNickname(),
                comment.getContent(), comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
    }
}
