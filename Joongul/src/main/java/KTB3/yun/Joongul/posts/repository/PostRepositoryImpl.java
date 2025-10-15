package KTB3.yun.Joongul.posts.repository;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.posts.domain.Post;
import KTB3.yun.Joongul.posts.domain.PostData;
import KTB3.yun.Joongul.posts.dto.PostDetailResponseDto;
import KTB3.yun.Joongul.posts.dto.PostSimpleResponseDto;
import KTB3.yun.Joongul.posts.dto.PostUpdateRequestDto;
import KTB3.yun.Joongul.posts.dto.PostWriteRequestDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static KTB3.yun.Joongul.posts.domain.PostData.postSequence;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final MemberRepository memberRepository;

    public PostRepositoryImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public List<PostSimpleResponseDto> findAll() {
        return PostData.POSTS.values().stream()
                .map(PostSimpleResponseDto::from).toList();
    }

    @Override
    public PostDetailResponseDto findById(Long postId) {
        Optional<Post> post = Optional.ofNullable(PostData.POSTS.get(postId));
        if (post.isEmpty()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, "존재하지 않는 리소스입니다.");
        }
        Post postData = post.get();
        postData.setViews(post.get().getViews()+1);
        return PostDetailResponseDto.from(post.get());
    }

    @Override
    synchronized public PostDetailResponseDto save(PostWriteRequestDto dto, Long memberId) {
        PostData.POSTS.put(postSequence, new Post(memberRepository.getMemberInfo(memberId), postSequence,
                dto.getTitle(), memberRepository.getMemberInfo(memberId).getNickname(),
                dto.getContent(), dto.getPostImage(), 0, 0, 0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), new ArrayList<>()));

        Post post = PostData.POSTS.get(postSequence);
        postSequence++;

        return PostDetailResponseDto.from(post);
    }

    @Override
    public PostDetailResponseDto update(Long postId, PostUpdateRequestDto dto, Long memberId) {
        Post oldPost = PostData.POSTS.get(postId);
        PostData.POSTS.put(postId, new Post(memberRepository.getMemberInfo(memberId), postId,
                dto.getTitle(), memberRepository.getMemberInfo(memberId).getNickname(),
                dto.getContent(), dto.getPostImage(), oldPost.getLikes(), oldPost.getComments(), oldPost.getViews(),
                oldPost.getCreatedAt(), oldPost.getCommentsList()));

        Post newPost = PostData.POSTS.get(postId);

        return PostDetailResponseDto.from(newPost);
    }

    @Override
    public void delete(Long postId) {
        PostData.POSTS.remove(postId);
    }

    @Override
    public Long findMemberIdByPostId(Long postId) {
        Post post = PostData.POSTS.get(postId);
        return post.getMember().getMemberId();
    }
}
