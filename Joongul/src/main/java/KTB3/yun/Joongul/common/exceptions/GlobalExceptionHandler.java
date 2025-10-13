package KTB3.yun.Joongul.common.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<CustomErrorResponse> handleException(ApplicationException ex) {
        ErrorCode code = ex.getErrorCode();
        CustomErrorResponse body = new CustomErrorResponse(code.getStatus(), ex.getMessage());
        return ResponseEntity.status(code.getStatus()).body(body);
    }
}
