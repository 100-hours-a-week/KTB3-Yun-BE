package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.members.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입에 성공한다")
    void signup() {
    }

    @Test
    void getMemberInfo() {
    }

    @Test
    void updateMemberInfo() {
    }

    @Test
    void modifyPassword() {
    }

    @Test
    void withdraw() {
    }

    @Test
    void isCorrectMember() {
    }

    @Test
    void findIdByEmail() {
    }

    @Test
    void isValidPassword() {
    }
}