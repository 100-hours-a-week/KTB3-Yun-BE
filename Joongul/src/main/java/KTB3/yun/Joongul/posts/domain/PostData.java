package KTB3.yun.Joongul.posts.domain;

import java.util.LinkedHashMap;
import java.util.Map;

public class PostData {

    public static Map<Long, Post> POSTS = new LinkedHashMap<>();
    public static Long postSequence = 5L;

    static {
        POSTS.put(1L, new Post());
        POSTS.put(2L, new Post());
        POSTS.put(3L, new Post());
        POSTS.put(4L, new Post());
    }
}
