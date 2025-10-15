package KTB3.yun.Joongul.posts.controller;

import KTB3.yun.Joongul.common.dto.ApiResponseDto;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.posts.dto.PostDetailResponseDto;
import KTB3.yun.Joongul.posts.dto.PostSimpleResponseDto;
import KTB3.yun.Joongul.posts.dto.PostUpdateRequestDto;
import KTB3.yun.Joongul.posts.dto.PostWriteRequestDto;
import KTB3.yun.Joongul.posts.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostSimpleResponseDto>> getPostList(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        List<PostSimpleResponseDto> postsList = postService.getAllPosts();
        return ResponseEntity.ok(postsList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponseDto> getPostDetail(@PathVariable(name = "id") Long postId,
                                                               HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        PostDetailResponseDto post = postService.getDetailPost(postId);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<PostDetailResponseDto>> savePost(@RequestBody @Valid PostWriteRequestDto postWriteRequestDto,
                                                          HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long loginId = (Long) session.getAttribute("USER_ID");
        PostDetailResponseDto savedPost = postService.savePost(postWriteRequestDto, loginId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>("post_write_success", savedPost));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PostDetailResponseDto>> updatePost(@PathVariable(name = "id") Long postId,
                                                                            @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto,
                                                                            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long loginId = (Long) session.getAttribute("USER_ID");
        Long postMemberId = postService.getMemberId(postId);
        if (!loginId.equals(postMemberId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, "잘못된 접근입니다.");
        }
        PostDetailResponseDto updatedPost = postService.updatePost(postId, postUpdateRequestDto, loginId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>("post_update_success", updatedPost));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable(name = "id") Long postId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_REQUEST, "로그인이 필요합니다.");
        }
        Long loginId = (Long) session.getAttribute("USER_ID");
        Long postMemberId = postService.getMemberId(postId);
        if (!loginId.equals(postMemberId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_REQUEST, "잘못된 접근입니다.");
        }
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
