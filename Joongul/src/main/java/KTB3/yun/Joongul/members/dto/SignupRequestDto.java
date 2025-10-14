package KTB3.yun.Joongul.members.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Pattern(
            regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "올바른 이메일 주소 형식을 입력해주세요.(예: example@example.com)")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;<>?,.]).{8,20}$",
            message = "비밀번호는 8자 이상 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호를 한 번 더 입력해주세요.")
    private String confirmPassword;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Pattern(
            regexp = "^[^\\s]+$",
            message = "띄어쓰기를 없애주세요."
    )
    @Size(min = 2, max = 10, message = "닉네임은 최대 10자까지 작성 가능합니다.")
    private String nickname;

    private String profileImage;
}
