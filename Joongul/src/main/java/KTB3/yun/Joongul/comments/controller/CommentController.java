package KTB3.yun.Joongul.comments.controller;

import KTB3.yun.Joongul.comments.dto.CommentResponseDto;
import KTB3.yun.Joongul.comments.dto.CommentUpdateRequestDto;
import KTB3.yun.Joongul.comments.dto.CommentWriteRequestDto;
import KTB3.yun.Joongul.comments.service.CommentService;
import KTB3.yun.Joongul.common.dto.ApiResponseDto;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> writeComment(@PathVariable(name = "postId") Long postId,
                                                                           @RequestBody CommentWriteRequestDto dto,
                                                                           HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long memberId = (Long) session.getAttribute("USER_ID");
        CommentResponseDto comment = commentService.writeComment(postId, dto, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>("comment_write_success", comment));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> updateComment(@PathVariable(name = "postId") Long postId,
                                                                            @PathVariable(name = "commentId") Long commentId,
                                                                            @RequestBody CommentUpdateRequestDto dto,
                                                                            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long memberId = (Long) session.getAttribute("USER_ID");
        if (!commentService.isValidMember(commentId, memberId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, "잘못된 접근입니다.");
        }
        CommentResponseDto comment = commentService.updateComment(postId, commentId, dto, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>("comment_update_success", comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "postId") Long postId,
                                              @PathVariable(name = "commentId") Long commentId,
                                              HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long memberId = (Long) session.getAttribute("USER_ID");
        if (!commentService.isValidMember(commentId, memberId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, "잘못된 접근입니다.");
        }
        commentService.deleteComment(commentId, postId);
        return ResponseEntity.noContent().build();
    }
}
