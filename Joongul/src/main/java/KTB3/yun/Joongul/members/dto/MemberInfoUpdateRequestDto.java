package KTB3.yun.Joongul.members.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoUpdateRequestDto {
    private Long memberId;
    private String nickname;
    private String profileImage;
}
