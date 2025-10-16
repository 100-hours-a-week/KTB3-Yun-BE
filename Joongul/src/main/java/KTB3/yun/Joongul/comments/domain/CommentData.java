package KTB3.yun.Joongul.comments.domain;

import KTB3.yun.Joongul.members.domain.MemberData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentData {

    public static Map<Long, Comment> COMMENTS = new HashMap<>();
    //키: postId, 값: 해당 post에 달린 commentId의 List
    public static Map<Long, List<Long>> COMMENT_IDS_IN_POST =  new HashMap<>();
    public static Long commentSequence = 6L;

    static {
        COMMENTS.put(1L, new Comment(1L, 1L, 1L, MemberData.MEMBERS.get(1L).getNickname(),
                "테스트 댓글 1", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        COMMENTS.put(2L, new Comment(1L, 1L, 2L, MemberData.MEMBERS.get(1L).getNickname(),
                "테스트 댓글 2", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        COMMENTS.put(3L, new Comment(2L, 1L, 3L, MemberData.MEMBERS.get(2L).getNickname(),
                "테스트 댓글 3", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        COMMENTS.put(4L, new Comment(1L, 2L, 4L, MemberData.MEMBERS.get(1L).getNickname(),
                "테스트 댓글 4", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        COMMENTS.put(5L, new Comment(2L, 2L, 5L, MemberData.MEMBERS.get(2L).getNickname(),
                "테스트 댓글 5", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        COMMENT_IDS_IN_POST.put(1L, List.of(1L, 2L, 3L));
        COMMENT_IDS_IN_POST.put(2L, List.of(4L, 5L));
    }
}
