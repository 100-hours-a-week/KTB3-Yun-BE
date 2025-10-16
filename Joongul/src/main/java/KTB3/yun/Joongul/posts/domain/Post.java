package KTB3.yun.Joongul.posts.domain;

import KTB3.yun.Joongul.comments.dto.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Post {
    private Long memberId;

    private Long postId;
    private String title;
    private String nickname;
    private String content;
    private String postImage;
    private int likes;
    private int comments;
    private int views;
    private String createdAt;
    private List<CommentResponseDto> commentsList;
}
