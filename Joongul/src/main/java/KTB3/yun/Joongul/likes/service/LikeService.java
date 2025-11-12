package KTB3.yun.Joongul.likes.service;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.likes.domain.Like;
import KTB3.yun.Joongul.likes.repository.LikeRepository;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.posts.domain.Post;
import KTB3.yun.Joongul.posts.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, MemberRepository memberRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void toggleLike(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        Like like = Like.builder()
                .post(post)
                .member(member)
                .build();

        likeRepository.save(like);
        post.increaseLikes();
    }

    @Transactional
    public void untoggleLike(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        Like like = likeRepository.findByPost_PostIdAndMember_MemberId(postId, memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        likeRepository.delete(like);
        post.decreaseLikes();
    }

    public Boolean isLiked(Long postId, Long memberId) {
        return likeRepository.existsByPost_PostIdAndMember_MemberId(postId, memberId);
    }
}
