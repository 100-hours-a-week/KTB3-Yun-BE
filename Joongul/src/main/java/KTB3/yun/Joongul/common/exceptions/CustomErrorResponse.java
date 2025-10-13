package KTB3.yun.Joongul.common.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CustomErrorResponse {
    private final int status;
    private final String message;
    private final LocalDateTime timestamp;
    public CustomErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
