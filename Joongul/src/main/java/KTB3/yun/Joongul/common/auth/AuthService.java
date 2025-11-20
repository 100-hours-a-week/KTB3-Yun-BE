package KTB3.yun.Joongul.common.auth;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public Long getMemberId(HttpServletRequest request) {
        return jwtTokenProvider.extractMemberId(request);
    }

    public void checkAuthority(HttpServletRequest request, Long id) {
        Long loginId = jwtTokenProvider.extractMemberId(request);
        if (!loginId.equals(id)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, ErrorCode.FORBIDDEN_REQUEST.getMessage());
        }
    }
}
