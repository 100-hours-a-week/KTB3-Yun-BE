package KTB3.yun.Joongul.likes.controller;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.likes.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{id}/likes")
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

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

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long postId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long memberId = (Long) session.getAttribute("USER_ID");
        likeService.untoggleLike(postId, memberId);
        return ResponseEntity.noContent().build();
    }
}
