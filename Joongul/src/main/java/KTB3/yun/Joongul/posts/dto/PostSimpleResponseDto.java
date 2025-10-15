package KTB3.yun.Joongul.posts.dto;

import KTB3.yun.Joongul.posts.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostSimpleResponseDto {
    private Long postId;
    private String title;
    private String nickname;
    private int likes;
    private int comments;
    private int views;
    private String createdAt;

    public static PostSimpleResponseDto from(Post post){
        return new PostSimpleResponseDto(post.getPostId(), post.getTitle(), post.getNickname(),
                post.getLikes(), post.getComments(), post.getViews(), post.getCreatedAt());
    }
}
