package KTB3.yun.Joongul.members.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequestDto {

    @Email
    private String email;
    private String password;
    private String confirmPassword;
    private String nickname;
    private String profileImage;
}
