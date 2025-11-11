package KTB3.yun.Joongul.posts.controller;

import KTB3.yun.Joongul.common.auth.AuthService;
import KTB3.yun.Joongul.common.dto.ApiResponseDto;
import KTB3.yun.Joongul.posts.dto.PostDetailResponseDto;
import KTB3.yun.Joongul.posts.dto.PostSimpleResponseDto;
import KTB3.yun.Joongul.posts.dto.PostUpdateRequestDto;
import KTB3.yun.Joongul.posts.dto.PostWriteRequestDto;
import KTB3.yun.Joongul.posts.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post-Controller", description = "Post CRUD API")
@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://127.0.0.1:5500/", allowCredentials = "true")
public class PostController {
    private final PostService postService;
    private final AuthService authService;

    public PostController(PostService postService, AuthService authService) {
        this.postService = postService;
        this.authService = authService;
    }

    @Operation(summary = "게시글 목록 조회 API")
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<PostSimpleResponseDto>>> getPostList(HttpServletRequest request) {
        authService.checkLoginUser(request);
        List<PostSimpleResponseDto> postsList = postService.getAllPosts();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>("posts_list_success", postsList));
    }

    @Operation(summary = "게시글 단건 상세 조회 API")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PostDetailResponseDto>> getPostDetail(@PathVariable(name = "id") Long postId,
                                                               HttpServletRequest request) {
        authService.checkLoginUser(request);
        PostDetailResponseDto post = postService.getDetailPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>("post_detail_success", post));
    }

    @Operation(summary = "게시글 작성 API")
    @PostMapping
    public ResponseEntity<ApiResponseDto<PostDetailResponseDto>> savePost(@RequestBody @Valid PostWriteRequestDto postWriteRequestDto,
                                                          HttpServletRequest request) {
        authService.checkLoginUser(request);
        Long memberId = authService.getMemberId(request);
        PostDetailResponseDto savedPost = postService.savePost(postWriteRequestDto, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.LOCATION)
                .header(HttpHeaders.LOCATION, "/posts/" + savedPost.getPostId())
                .body(new ApiResponseDto<>("post_write_success", savedPost));
    }

    @Operation(summary = "게시글 수정 API")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<PostDetailResponseDto>> updatePost(@PathVariable(name = "id") Long postId,
                                                                            @RequestBody @Valid PostUpdateRequestDto postUpdateRequestDto,
                                                                            HttpServletRequest request) {
        authService.checkLoginUser(request);
        Long postMemberId = postService.getMemberId(postId);
        authService.checkAuthority(request, postMemberId);
        PostDetailResponseDto updatedPost = postService.updatePost(postId, postUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDto<>("post_update_success", updatedPost));
    }

    @Operation(summary = "게시글 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable(name = "id") Long postId, HttpServletRequest request) {
        authService.checkLoginUser(request);
        Long postMemberId = postService.getMemberId(postId);
        authService.checkAuthority(request, postMemberId);
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }
}
