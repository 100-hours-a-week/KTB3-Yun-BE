package KTB3.yun.Joongul.members.domain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

public class MemberData {

    public static Map<Long, Member> MEMBERS = new HashMap<>();
    volatile public static Long sequence = 3L;

    //isExist.. 관련 메서드들을 작성하며 email과 nickname을 key로 하는 Map이 필요하다고 느꼈습니다.
    public static Map<String, Long> EMAILS = new HashMap<>();
    public static Map<String, Long> NICKNAMES = new HashMap<>();

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    static {
        MEMBERS.put(1L, new Member(1L, "testone@test.com", passwordEncoder.encode("Test111!"),
                "테스트1", "https://image.kr/test1"));
        MEMBERS.put(2L, new Member(2L, "testtwo@test.com", passwordEncoder.encode("Test222!"),
                "테스트2", "https://image.kr/test2"));
        EMAILS.put(MEMBERS.get(1L).getEmail(), 1L);
        EMAILS.put(MEMBERS.get(2L).getEmail(), 2L);
        NICKNAMES.put(MEMBERS.get(1L).getNickname(), 1L);
        NICKNAMES.put(MEMBERS.get(2L).getNickname(), 2L);
    }
}
