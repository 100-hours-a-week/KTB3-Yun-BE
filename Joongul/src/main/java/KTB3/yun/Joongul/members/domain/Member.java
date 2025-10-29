package KTB3.yun.Joongul.members.domain;

import KTB3.yun.Joongul.comments.domain.Comment;
import KTB3.yun.Joongul.likes.domain.Like;
import KTB3.yun.Joongul.posts.domain.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    private String email;
    private String password;
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @OneToMany(mappedBy = "member")
    private List<Post> postsList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> commentsList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Like> likesList = new ArrayList<>();

    public void updateMemberInfo(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void modifyPassword(String password) {
        this.password = password;
    }
}
