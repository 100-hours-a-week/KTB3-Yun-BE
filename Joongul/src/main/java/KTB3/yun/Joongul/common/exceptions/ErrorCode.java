package KTB3.yun.Joongul.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(400, "잘못된 입력값입니다."),
    UNAUTHORIZED_REQUEST(401, "로그인이 필요합니다."),
    FORBIDDEN_REQUEST(403, "잘못된 접근입니다."),
    NOT_FOUNT(404, "존재하지 않는 리소스입니다."),
    DUPLICATE_EMAIL(409, "중복된 이메일입니다."),
    DUPLICATE_NICKNAME(409, "중복된 닉네임입니다."),
    INVALID_EMAIL(422, "올바르지 않은 이메일 형식입니다."),
    INVALID_PASSWORD(422, "올바르지 않은 비밀번호 형식입니다."),
    INVALID_NICKNAME(422, "올바르지 않은 닉네임 형식입니다."),
    USING_PASSWORD(422, "이미 사용 중인 비밀번호입니다."),
    INTERNAL_SERVER_ERROR(500, "처리 도중 예외가 발생했습니다.");

    private final int status;
    private final String message;
}
