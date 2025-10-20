package KTB3.yun.Joongul.likes.controller;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.likes.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like-Controller", description = "Like CRUD API")
@RestController
@RequestMapping("/posts/{id}/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @Operation(summary = "좋아요 추가 API")
    @PutMapping
    public ResponseEntity<Void> like(@PathVariable(name = "id") Long postId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long memberId = (Long) session.getAttribute("USER_ID");
        likeService.toggleLike(postId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "좋아요 취소 API")
    @DeleteMapping
    public ResponseEntity<Void> unlike(@PathVariable(name = "id") Long postId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long memberId = (Long) session.getAttribute("USER_ID");
        likeService.untoggleLike(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}
