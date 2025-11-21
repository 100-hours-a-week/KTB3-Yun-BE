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
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_image", nullable = true)
    private String profileImage;

    @Column(name = "is_deleted", nullable = true)
    private Boolean isDeleted;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

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

    public void deleteMember() {
        this.isDeleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;

        return Objects.equals(memberId, member.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}
