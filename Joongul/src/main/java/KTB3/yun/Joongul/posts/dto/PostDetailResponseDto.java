package KTB3.yun.Joongul.posts.dto;

import KTB3.yun.Joongul.comments.domain.Comment;
import KTB3.yun.Joongul.posts.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostDetailResponseDto {
    private Long postId;
    private String title;
    private String nickname;
    private String content;
    private String postImage;
    private int likes;
    private int comments;
    private int views;
    private String createdAt;
    private List<Comment> commentsList;

    public static PostDetailResponseDto from(Post post){
        return new PostDetailResponseDto(post.getPostId(), post.getTitle(), post.getNickname(), post.getContent(),
                post.getPostImage(), post.getLikes(), post.getComments(), post.getViews(),
                post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                post.getCommentsList());
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
