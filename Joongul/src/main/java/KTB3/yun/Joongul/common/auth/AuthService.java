package KTB3.yun.Joongul.common.auth;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class AuthService {

    private final static String USER_ID = "USER_ID";

    public Long getMemberId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute(USER_ID);
    }

    public void checkLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, ErrorCode.UNAUTHORIZED_REQUEST.getMessage());
        }
    }

    public void checkAuthority(HttpServletRequest request, Long id) {
        HttpSession session = request.getSession(false);
        Long loginId = (Long) session.getAttribute(USER_ID);
        if (!loginId.equals(id)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, ErrorCode.FORBIDDEN_REQUEST.getMessage());
        }
    }
}
