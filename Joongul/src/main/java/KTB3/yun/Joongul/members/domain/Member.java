package KTB3.yun.Joongul.members.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Member {
    private Long memberId;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;
}
