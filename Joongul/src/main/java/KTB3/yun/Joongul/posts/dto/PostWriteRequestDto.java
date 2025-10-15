package KTB3.yun.Joongul.posts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostWriteRequestDto {
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 26, message = "제목은 최대 26자까지입니다.")
    private String title;
    private String nickname;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    private String postImage;
}
