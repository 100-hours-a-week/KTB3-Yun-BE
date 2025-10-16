package KTB3.yun.Joongul.comments.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Comment {
    private Long memberId;
    private Long postId;

    private Long commentId;
    private String nickname;
    private String content;
    private String createdAt;
}
