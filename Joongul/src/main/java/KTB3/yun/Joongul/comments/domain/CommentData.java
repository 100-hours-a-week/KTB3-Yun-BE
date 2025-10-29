package KTB3.yun.Joongul.comments.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentData {

    public static Map<Long, Comment> COMMENTS = new HashMap<>();
    //키: postId, 값: 해당 post에 달린 commentId의 List
    public static Map<Long, List<Long>> COMMENT_IDS_IN_POST = new HashMap<>();
    public static Long commentSequence = 6L;

    static {
        COMMENTS.put(1L, new Comment());
        COMMENTS.put(2L, new Comment());
        COMMENTS.put(3L, new Comment());
        COMMENTS.put(4L, new Comment());
        COMMENTS.put(5L, new Comment());

        COMMENT_IDS_IN_POST.put(1L, new ArrayList<>(List.of(1L, 2L, 3L)));
        COMMENT_IDS_IN_POST.put(2L, new ArrayList<>(List.of(4L, 5L)));
        COMMENT_IDS_IN_POST.put(3L, new ArrayList<>(List.of()));
        COMMENT_IDS_IN_POST.put(4L, new ArrayList<>(List.of()));
    }
}
