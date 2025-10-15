package KTB3.yun.Joongul.posts.service;

import KTB3.yun.Joongul.posts.dto.PostDetailResponseDto;
import KTB3.yun.Joongul.posts.dto.PostSimpleResponseDto;
import KTB3.yun.Joongul.posts.dto.PostUpdateRequestDto;
import KTB3.yun.Joongul.posts.dto.PostWriteRequestDto;
import KTB3.yun.Joongul.posts.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostSimpleResponseDto> getAllPosts() {
        return postRepository.findAll();
    }

    public PostDetailResponseDto getDetailPost(Long postId) {
        return postRepository.findById(postId);
    }

    public PostDetailResponseDto savePost(PostWriteRequestDto postWriteRequestDto, Long memberId) {
        return postRepository.save(postWriteRequestDto, memberId);
    }

    public PostDetailResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto, Long memberId) {
        return postRepository.update(postId, postUpdateRequestDto, memberId);
    }

    public void deletePost(Long postId) {
        postRepository.delete(postId);
    }

    public Long getMemberId(Long postId) {
        return postRepository.findMemberIdByPostId(postId);
    }
}
