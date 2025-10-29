package KTB3.yun.Joongul.posts.domain;

import KTB3.yun.Joongul.comments.domain.Comment;
import KTB3.yun.Joongul.likes.domain.Like;
import KTB3.yun.Joongul.members.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    private String title;
    private String nickname;
    private String content;

    @Column(name = "post_image")
    private String postImage;

    private int likes;
    private int comments;
    private int views;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post")
    private List<Comment> commentsList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Like> likeslist = new ArrayList<>();
}
