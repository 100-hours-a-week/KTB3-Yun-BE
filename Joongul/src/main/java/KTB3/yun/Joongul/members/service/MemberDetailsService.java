package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.MemberDetails;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return new MemberDetails(member);
    }
}
