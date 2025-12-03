package KTB3.yun.Joongul.members.service;

import KTB3.yun.Joongul.common.auth.JwtTokenProvider;
import KTB3.yun.Joongul.common.dto.JwtToken;
import KTB3.yun.Joongul.common.exceptions.ApplicationException;
import KTB3.yun.Joongul.common.exceptions.ErrorCode;
import KTB3.yun.Joongul.members.domain.Member;
import KTB3.yun.Joongul.members.domain.MemberDetails;
import KTB3.yun.Joongul.members.domain.RefreshToken;
import KTB3.yun.Joongul.members.dto.*;
import KTB3.yun.Joongul.members.repository.MemberRepository;
import KTB3.yun.Joongul.members.repository.RefreshTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public MemberService(MemberRepository memberRepository, AuthenticationManager authenticationManager,
                         JwtTokenProvider jwtTokenProvider, BCryptPasswordEncoder passwordEncoder,
                         RefreshTokenRepository refreshTokenRepository) {
        this.memberRepository = memberRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void signup(SignUpRequestDto signupRequestDto) {
        boolean isExistEmail = memberRepository.existsByEmail(signupRequestDto.getEmail());
        boolean isExistNickname = memberRepository.existsByNickname(signupRequestDto.getNickname());
        boolean isSameWithConfirmPassword = signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword());

        if (isExistEmail) {
            throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL, ErrorCode.DUPLICATE_EMAIL.getMessage());
        } else if (isExistNickname) {
            throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, ErrorCode.DUPLICATE_NICKNAME.getMessage());
        } else if (!isSameWithConfirmPassword) {
            throw new ApplicationException(ErrorCode.NOT_SAME_WITH_CONFIRM, ErrorCode.NOT_SAME_WITH_CONFIRM.getMessage());
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        Member member = Member.builder()
                .email(signupRequestDto.getEmail())
                .password(encodedPassword)
                .nickname(signupRequestDto.getNickname())
                .profileImage(signupRequestDto.getProfileImage())
                .isDeleted(Boolean.FALSE)
                .roles(roles)
                .build();
        memberRepository.save(member);
    }

    public MemberInfoResponseDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        if (member.getIsDeleted()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }

        return new MemberInfoResponseDto(member.getMemberId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage());
    }

    @Transactional
    public void updateMemberInfo(MemberInfoUpdateRequestDto dto, Long memberId) {
        boolean isExistNickname = memberRepository.existsByNickname(dto.getNickname());

        if (isExistNickname) {
            throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, ErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        if (member.getIsDeleted()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }

        member.updateMemberInfo(dto.getNickname(), dto.getProfileImage());
    }

    @Transactional
    public void modifyPassword(PasswordUpdateRequestDto passwordUpdateRequestDto, Long memberId) {
        boolean isUsedPassword = isUsedPassword(memberId, passwordUpdateRequestDto.getPassword());
        boolean isSameWithConfirmPassword = passwordUpdateRequestDto.getPassword().equals(passwordUpdateRequestDto.getConfirmPassword());

        if (isUsedPassword) {
            throw new ApplicationException(ErrorCode.USING_PASSWORD, ErrorCode.USING_PASSWORD.getMessage());
        }
        else if (!isSameWithConfirmPassword) {
            throw new ApplicationException(ErrorCode.NOT_SAME_WITH_CONFIRM, ErrorCode.NOT_SAME_WITH_CONFIRM.getMessage());
        }

        String newEncodedPassword = passwordEncoder.encode(passwordUpdateRequestDto.getPassword());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()));

        if (member.getIsDeleted()) {
            throw new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage());
        }

        member.modifyPassword(newEncodedPassword);
    }

    @Transactional
    public void withdraw(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage())).deleteMember();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        MemberDetails principal = (MemberDetails) authentication.getPrincipal();
        String nickname = principal.getMember().getNickname();

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(jwtToken.getRefreshToken())
                .createdAt(System.currentTimeMillis())
                .expiresAt(jwtToken.getRefreshTokenExpireTime())
                .member(((MemberDetails) authentication.getPrincipal()).getMember())
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new LoginResponseDto(jwtToken.getGrantType(), jwtToken.getAccessToken(), jwtToken.getRefreshToken(),
                jwtToken.getRefreshTokenExpireTime(), email, nickname);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }


    public boolean isUsedPassword(Long memberId, String password) {
        String savedPassword = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, ErrorCode.NOT_FOUND.getMessage()))
                .getPassword();
        return passwordEncoder.matches(password, savedPassword);
    }
}
