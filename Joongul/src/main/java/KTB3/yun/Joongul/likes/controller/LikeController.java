package KTB3.yun.Joongul.likes.controller;

import KTB3.yun.Joongul.common.auth.AuthService;
import KTB3.yun.Joongul.likes.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Like-Controller", description = "Like CRUD API")
@RestController
@RequestMapping("/posts/{id}/likes")
public class LikeController {
    private final LikeService likeService;
    private final AuthService authService;

    public LikeController(LikeService likeService, AuthService authService) {
        this.likeService = likeService;
        this.authService = authService;
    }

    @Operation(summary = "좋아요 추가 API")
    @PutMapping
    public ResponseEntity<Void> like(@PathVariable(name = "id") Long postId, HttpServletRequest request) {
        authService.checkLoginUser(request);
        Long memberId = authService.getMemberId(request);
        likeService.toggleLike(postId, memberId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "좋아요 취소 API")
    @DeleteMapping
    public ResponseEntity<Void> unlike(@PathVariable(name = "id") Long postId, HttpServletRequest request) {
        authService.checkLoginUser(request);
        Long memberId = authService.getMemberId(request);
        likeService.untoggleLike(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}
