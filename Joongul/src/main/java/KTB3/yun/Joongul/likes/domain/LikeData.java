package KTB3.yun.Joongul.likes.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LikeData {

    //키: postId, 값: 해당 게시글에 좋아요를 누른 memberId Set
    public static Map<Long, Set<Long>> LIKES = new HashMap<>();

    static {
        LIKES.put(1L, new HashSet<>(Set.of(1L, 2L)));
        LIKES.put(2L, new HashSet<>(Set.of(1L)));
        LIKES.put(3L, new HashSet<>(Set.of()));
        LIKES.put(4L, new HashSet<>(Set.of()));
    }
}
