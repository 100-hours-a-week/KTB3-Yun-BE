package KTB3.yun.Joongul.posts.repository;

import KTB3.yun.Joongul.posts.dto.PostDetailResponseDto;
import KTB3.yun.Joongul.posts.dto.PostSimpleResponseDto;
import KTB3.yun.Joongul.posts.dto.PostUpdateRequestDto;
import KTB3.yun.Joongul.posts.dto.PostWriteRequestDto;

import java.util.List;

public interface PostRepository {
    List<PostSimpleResponseDto> findAll();
    PostDetailResponseDto findById(Long postId);
    PostDetailResponseDto save(PostWriteRequestDto postWriteRequestDto, Long memberId);
    PostDetailResponseDto update(Long postId, PostUpdateRequestDto postUpdateRequestDto, Long memberId);
    void delete(Long postId);

    Long findMemberIdByPostId(Long postId);
}
