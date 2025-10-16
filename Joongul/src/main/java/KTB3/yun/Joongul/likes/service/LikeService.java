package KTB3.yun.Joongul.likes.service;

import KTB3.yun.Joongul.likes.repository.LikeRepository;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public void toggleLike(Long postId, Long memberId) {
        likeRepository.toggleLike(postId, memberId);
    }

    public void untoggleLike(Long postId, Long memberId) {
        likeRepository.untoggleLike(postId, memberId);
    }
}
