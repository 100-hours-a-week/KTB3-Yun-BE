package KTB3.yun.Joongul.posts.service;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.posts.domain.Post;
import KTB3.yun.Joongul.posts.dto.PostDetailResponseDto;
import KTB3.yun.Joongul.posts.dto.PostSimpleResponseDto;
import KTB3.yun.Joongul.posts.dto.PostUpdateRequestDto;
import KTB3.yun.Joongul.posts.dto.PostWriteRequestDto;
import KTB3.yun.Joongul.posts.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostService(PostRepository postRepository, MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    public List<PostSimpleResponseDto> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .filter(post -> !post.getIsDeleted())
                .map(PostSimpleResponseDto::from)
                .toList();
    }

    @Transactional
    public PostDetailResponseDto getDetailPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        if (post.getIsDeleted()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }

        post.increaseViews();

        return new PostDetailResponseDto(post.getPostId(), post.getTitle(), post.getNickname(), post.getContent(),
                post.getPostImage(), post.getLikes(), post.getComments(), post.getViews(),
                post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), post.getCommentsList());
    }

    public PostDetailResponseDto savePost(PostWriteRequestDto postWriteRequestDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));
        Post newPost = Post.builder()
                .title(postWriteRequestDto.getTitle())
                .nickname(member.getNickname())
                .content(postWriteRequestDto.getContent())
                .postImage(postWriteRequestDto.getPostImage())
                .likes(0)
                .comments(0)
                .views(0)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .commentsList(new ArrayList<>())
                .member(member)
                .build();
        postRepository.save(newPost);
        return PostDetailResponseDto.from(newPost);
    }

    @Transactional
    public PostDetailResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        if (post.getIsDeleted()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }

        Post updatedPost = post.updatePost(postUpdateRequestDto.getTitle(), postUpdateRequestDto.getContent(),
                postUpdateRequestDto.getPostImage());

        return PostDetailResponseDto.from(updatedPost);
    }

    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public Long getMemberId(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()))
                .getMember().getMemberId();
    }
}
