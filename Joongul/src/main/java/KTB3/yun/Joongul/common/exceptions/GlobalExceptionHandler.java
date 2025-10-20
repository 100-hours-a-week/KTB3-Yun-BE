package KTB3.yun.Joongul.common.exceptions;

import KTB3.yun.Joongul.comments.controller.CommentController;
import KTB3.yun.Joongul.likes.controller.LikeController;
import KTB3.yun.Joongul.members.controller.MemberController;
import KTB3.yun.Joongul.posts.controller.PostController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class,
        basePackageClasses = {CommentController.class, MemberController.class, PostController.class, LikeController.class})
public class GlobalExceptionHandler {

    //각 상황마다 원하는 예외를 던져주기 위해 작성했습니다.
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<CustomErrorResponse> handleApplicationException(ApplicationException ex) {
        ErrorCode code = ex.getErrorCode();
        CustomErrorResponse body = new CustomErrorResponse(code.getStatus().value(), ex.getMessage());
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    //@Valid의 유효성 검증에 실패하면 각 필드에 맞게 메시지를 함께 던져주기 위해 작성했습니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>(); //dto와 동일한 순서로 보내주기 위해 LinkedHashMap을 사용
        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
