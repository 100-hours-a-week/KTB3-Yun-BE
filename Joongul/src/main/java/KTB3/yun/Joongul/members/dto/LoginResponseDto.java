package KTB3.yun.Joongul.members.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpireTime;

    private String email;
    private String nickname;
}
