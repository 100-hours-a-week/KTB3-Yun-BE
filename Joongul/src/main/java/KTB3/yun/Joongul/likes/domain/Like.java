package KTB3.yun.Joongul.likes.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Like {
    private Long memberId;
    private Long postId;

    private Long likeId;
}
