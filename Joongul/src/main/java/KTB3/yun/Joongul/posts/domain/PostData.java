package KTB3.yun.Joongul.posts.domain;

import KTB3.yun.Joongul.members.domain.MemberData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostData {

    public static Map<Long, Post> POSTS = new LinkedHashMap<>();
    public static Long postSequence = 5L;

    static {
        POSTS.put(1L, new Post(MemberData.MEMBERS.get(1L), 1L, "Test 1", MemberData.MEMBERS.get(1L).getNickname(),
                "테스트 1", "https://image.kr/post1", 0, 0, 0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), new ArrayList<>()));
        POSTS.put(2L, new Post(MemberData.MEMBERS.get(1L), 2L, "Test 2", MemberData.MEMBERS.get(1L).getNickname(),
                "테스트 2", "https://image.kr/post2", 0, 0, 0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), new ArrayList<>()));
        POSTS.put(3L, new Post(MemberData.MEMBERS.get(2L), 3L, "Test 3", MemberData.MEMBERS.get(2L).getNickname(),
                "테스트 3", "https://image.kr/post3", 0, 0, 0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), new ArrayList<>()));
        POSTS.put(4L, new Post(MemberData.MEMBERS.get(2L), 4L, "Test 4", MemberData.MEMBERS.get(2L).getNickname(),
                "테스트 4", "https://image.kr/post4", 0, 0, 0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), new ArrayList<>()));

    }
}
