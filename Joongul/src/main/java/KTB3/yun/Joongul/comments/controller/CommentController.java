package KTB3.yun.Joongul.comments.controller;

import KTB3.yun.Joongul.comments.dto.CommentResponseDto;
import KTB3.yun.Joongul.comments.dto.CommentUpdateRequestDto;
import KTB3.yun.Joongul.comments.dto.CommentWriteRequestDto;
import KTB3.yun.Joongul.comments.service.CommentService;
import KTB3.yun.Joongul.common.auth.AuthService;
import KTB3.yun.Joongul.common.dto.ApiResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment-Controller", description = "Comment CRUD API")
@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;
    private final AuthService authService;

    public CommentController(CommentService commentService, AuthService authService) {
        this.commentService = commentService;
        this.authService = authService;
    }

    @Operation(summary = "댓글 작성 API")
    @PostMapping
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> writeComment(@PathVariable(name = "postId") Long postId,
                                                                           @RequestBody CommentWriteRequestDto dto,
                                                                           HttpServletRequest request) {
        Long memberId = authService.getMemberId(request);
        CommentResponseDto comment = commentService.writeComment(postId, dto, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>("comment_write_success", comment));
    }

    @Operation(summary = "댓글 수정 API")
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> updateComment(@PathVariable(name = "postId") Long postId,
                                                                            @PathVariable(name = "commentId") Long commentId,
                                                                            @RequestBody CommentUpdateRequestDto dto,
                                                                            HttpServletRequest request) {
        Long memberId = commentService.getMemberId(commentId);
        authService.checkAuthority(request, memberId);
        CommentResponseDto comment = commentService.updateComment(commentId, postId, dto);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>("comment_update_success", comment));
    }

    @Operation(summary = "댓글 삭제 API")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(name = "postId") Long postId,
                                              @PathVariable(name = "commentId") Long commentId,
                                              HttpServletRequest request) {
        Long memberId = commentService.getMemberId(commentId);
        authService.checkAuthority(request, memberId);
        commentService.deleteComment(commentId, postId);
        return ResponseEntity.noContent().build();
    }
}
