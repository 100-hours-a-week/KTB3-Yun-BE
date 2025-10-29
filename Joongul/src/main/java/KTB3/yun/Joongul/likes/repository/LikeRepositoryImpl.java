package KTB3.yun.Joongul.likes.repository;

import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.likes.domain.LikeData;
import KTB3.yun.Joongul.posts.domain.PostData;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class LikeRepositoryImpl implements LikeRepository {

    @Override
    public void toggleLike(Long postId, Long memberId) {
        if (PostData.POSTS.get(postId) == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }
        LikeData.LIKES.computeIfAbsent(postId, k -> new HashSet<>()).add(memberId);
//        PostData.POSTS.get(postId).setLikes(LikeData.LIKES.get(postId).size());
    }

    @Override
    public void untoggleLike(Long postId, Long memberId) {
        if (PostData.POSTS.get(postId) == null){
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }
        Set<Long> likes = LikeData.LIKES.get(postId);
        likes.remove(memberId);
//        PostData.POSTS.get(postId).setLikes(LikeData.LIKES.get(postId).size());
    }
}
