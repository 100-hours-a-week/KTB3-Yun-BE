package KTB3.yun.Joongul.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class JwtToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpireTime;
}
