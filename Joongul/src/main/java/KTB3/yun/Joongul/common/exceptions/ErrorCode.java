package KTB3.yun.Joongul.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_SAME_WITH_CONFIRM(HttpStatus.BAD_REQUEST, "비밀번호가 다릅니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 다릅니다."),
    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN_REQUEST(HttpStatus.FORBIDDEN, "잘못된 접근입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리소스입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임입니다."),
    USING_PASSWORD(HttpStatus.UNPROCESSABLE_ENTITY, "이미 사용 중인 비밀번호입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다");

    private final HttpStatusCode status;
    private final String message;
}
