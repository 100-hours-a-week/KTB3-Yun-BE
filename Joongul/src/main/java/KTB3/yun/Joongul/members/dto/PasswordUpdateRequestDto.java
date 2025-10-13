package KTB3.yun.Joongul.members.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordUpdateRequestDto {
    private Long memberId;
    private String password;
    private String confirmPassword;
}
