package KTB3.yun.Joongul.members.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoUpdateRequestDto {
    private Long memberId;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^[^\\s]+$",
            message = "띄어쓰기를 없애주세요."
    )
    @Size(min = 2, max = 10, message = "닉네임은 최대 10자까지 작성 가능합니다.")
    private String nickname;

    private String profileImage;
}
