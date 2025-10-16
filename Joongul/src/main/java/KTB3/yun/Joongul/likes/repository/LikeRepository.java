package KTB3.yun.Joongul.likes.repository;

public interface LikeRepository {
    void toggleLike(Long postId, Long memberId);
    void untoggleLike(Long postId, Long memberId);
}
