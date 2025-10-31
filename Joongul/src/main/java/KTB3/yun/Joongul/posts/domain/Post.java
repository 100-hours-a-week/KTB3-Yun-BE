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
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String content;

    @Column(name = "post_image", nullable = true)
    private String postImage;

    @Column(nullable = false)
    private int likes;

    @Column(nullable = false)
    private int comments;

    @Column(nullable = false)
    private int views;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "post")
    private List<Comment> commentsList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Like> likeslist = new ArrayList<>();

    public void addComment(Comment comment) {
        this.commentsList.add(comment);
    }

    public Post updatePost(String title, String content, String postImage) {
        this.title = title;
        this.content = content;
        this.postImage = postImage;

        return this;
    }

    public void increaseLikes() {
        this.likes++;
    }
    public void decreaseLikes() {
        this.likes--;
    }

    public void increaseComments() {
        this.comments++;
    }
    public void decreaseComments() {
        this.comments--;
    }

    public void increaseViews() {
        this.views++;
    }
}
