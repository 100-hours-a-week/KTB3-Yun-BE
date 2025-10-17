package KTB3.yun.Joongul.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentUpdateRequestDto {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
