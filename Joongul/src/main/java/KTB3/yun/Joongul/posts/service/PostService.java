package KTB3.yun.Joongul.posts.service;

import KTB3.yun.Joongul.comments.domain.Comment;
import KTB3.yun.Joongul.comments.dto.CommentResponseDto;
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
        return postRepository.findAllByIsDeletedFalse()
                .stream()
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

        List<CommentResponseDto> commentsDtoList = commentsListToDto(post);

        return new PostDetailResponseDto(post.getPostId(), post.getTitle(), post.getNickname(), post.getContent(),
                post.getPostImage(), post.getLikes(), post.getComments(), post.getViews(),
                post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")), commentsDtoList);
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
        return PostDetailResponseDto.from(newPost, new ArrayList<>());
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

        List<CommentResponseDto> commentsDtoList = commentsListToDto(post);

        return PostDetailResponseDto.from(updatedPost, commentsDtoList);
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

    private List<CommentResponseDto> commentsListToDto(Post post) {
        List<Comment> commentsList = post.getCommentsList();
        return commentsList.stream().map(CommentResponseDto::from).toList();
    }
}
