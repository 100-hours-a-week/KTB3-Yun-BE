package KTB3.yun.Joongul.members.controller;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.dto.RefreshTokenResponseDto;
import KTB3.yun.Joongul.members.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final TokenService tokenService;
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<RefreshTokenResponseDto> reissueToken(HttpServletRequest request,
                                                                HttpServletResponse response) throws ApplicationException {
        String oldRefreshToken = tokenService.extractRefreshToken(request);

        if (oldRefreshToken.equals("not_found")) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }

        RefreshTokenResponseDto refreshTokenResponseDto = tokenService.getNewToken(oldRefreshToken);
        String newRefreshToken = refreshTokenResponseDto.getRefreshToken();

        String setRefreshToken = "refreshToken=" + newRefreshToken + "; HttpOnly; " + "SameSite=Lax; "
                + "Path=/; " + "Max-Age=" + refreshTokenResponseDto.getRefreshTokenExpireTime();

        response.addHeader("Set-Cookie", setRefreshToken);
        return ResponseEntity.ok(refreshTokenResponseDto);
    }
}
