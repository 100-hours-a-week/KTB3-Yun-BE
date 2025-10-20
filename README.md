# 5주차 과제 : SOLID 기반 리팩토링 진행
SOLID 기반으로 4주차 과제 리팩토링을 진행한 브랜치입니다.
## 기존 코드의 문제점 및 리팩토링 대상 선정 이유
### 기존 코드
```java
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final BCryptPasswordEncoder passwordEncoder;

    public MemberRepositoryImpl(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

  @Override
  //memberSequence 변수의 메모리 가시성과 원자성 확보를 위해 synchronized를 적용했습니다.
  synchronized public void addMember(SignupRequestDto dto) {
    MemberData.MEMBERS.put(memberSequence, new Member(memberSequence, dto.getEmail(), passwordEncoder.encode(dto.getPassword()),
      dto.getNickname(), dto.getProfileImage()));
    MemberData.EMAILS.put(dto.getEmail(), memberSequence);
    MemberData.NICKNAMES.put(dto.getNickname(), memberSequence);
    memberSequence++;
  }

//...

  @Override
  public void modifyPassword(PasswordUpdateRequestDto dto, Long memberId) {
    Member member = MemberData.MEMBERS.get(memberId);
    member.setPassword(passwordEncoder.encode(dto.getPassword()));
  }

//...

  @Override
  public boolean alreadyUsingPassword(Long memberId, String password) {
    return passwordEncoder.matches(password, MemberData.MEMBERS.get(memberId).getPassword());
  }

  @Override
  public boolean isCorrectPassword(Long memberId, String password) {
    return passwordEncoder.matches(password, MemberData.MEMBERS.get(memberId).getPassword());
  }

//...
}
```
### 문제점
1. 기존 코드에서는 비밀번호 인코딩을 Repository 계층에서 진행하고 있었고, 그래서 `BCryptPasswordEncoder`도 해당 계층에 주입받는 구조였습니다.
2. 비밀번호 인코딩을 Repository 계층에서 진행하다 보니 회원가입 메서드가 `Member` Entity가 아닌 DTO를 넘겨받는 구조였습니다.
3. `alreadyUsingPassword()`와 `isCorrectPassword()`는 비즈니스 로직 검사를 위한 메서드인데 Repository에 작성되어 있었습니다.

### 선정 이유
1. Repository 계층은 DB 접근 및 수정에 대한 로직만 수행해야 하는데 비즈니스 로직 검사까지 진행하고 있으므로 SOLID 원칙 중 SRP-단일 책임 원칙-를 위반하고 있다고 보았습니다.
2. 회원가입 메서드인 `addMember()`에서 파라미터로 `SignupRequestDto`를 넘겨 받고 있습니다. 비즈니스 로직을 수행하여 DTO를 Entity로 바꾸는 것 또한 Service 계층에서 진행하는 것이 옳다고 생각했습니다.

## 리팩토링 진행 방향 및 과정
처음에는 선정 이유(2)를 미처 생각하지 못하고
DTO에 `Builder` 패턴을 적용하여 `MemberService`에서
```java
SignUpRequestDto.builder().password(encodedPassword).build();
```
와 같이 처리하려고 했습니다.

문제는 DTO 자체의 단일 책임 원칙을 고려하지 못했다는 것입니다.

해당 메서드에서 사용하는 DTO는 '클라이언트의 원본 요청을 담아 서버로 전달하는' 역할을 합니다.

즉, 기존에 담겨 있던 비밀번호를 인코딩한 뒤 `builder()`로 다시 담는 것은 DTO의 역할을 벗어난 것입니다.

생각이 여기까지 미치자 DTO와 Repository의 역할을 SRP 개념에서 다시 정리해 보게 되었습니다.

DTO는 '요청 혹은 응답 원본을 전달해주는 역할'을 하며,

Repository는 '데이터 저장소(DB)와 도메인(Entity)을 매핑해주는 역할'을 한다고 정리했습니다.

이렇게 정리를 하고 나니 기존 `MemberRepositoryImpl` 내의 메서드들이 `SignUpRequestDto`를 파라미터로 받고 있는 것이 적절하지 못한 구조라는 생각이 들었고 해당 부분까지 리팩토링 진행하였습니다.


## 리팩토링 성과
`MemberRepositoryImpl.java`
```java
@Repository
public class MemberRepositoryImpl implements MemberRepository {

  @Override
  //memberSequence 변수의 메모리 가시성과 원자성 확보를 위해 synchronized를 적용했습니다.
  synchronized public void addMember(Member member) {
    MemberData.MEMBERS.put(memberSequence, member);
    MemberData.EMAILS.put(member.getEmail(), memberSequence);
    MemberData.NICKNAMES.put(member.getNickname(), memberSequence);
    memberSequence++;
  }

//...

  @Override
  public void modifyPassword(String newPassword, Long memberId){
    Member member = MemberData.MEMBERS.get(memberId);
    member.setPassword(newPassword);
  }

//...
}
```
`MemberService.java`
```java
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
  }

//...

  public void signup(SignUpRequestDto signupRequestDto) {
    isExistEmail = memberRepository.existsByEmail(signupRequestDto.getEmail());
    isExistNickname = memberRepository.existsByNickname(signupRequestDto.getNickname());
    isSameWithConfirmPassword = signupRequestDto.getPassword().equals(signupRequestDto.getConfirmPassword());

    if (isExistEmail) {
      throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL, "중복된 이메일입니다.");
    } else if (isExistNickname) {
      throw new ApplicationException(ErrorCode.DUPLICATE_NICKNAME, "중복된 닉네임입니다.");
    } else if (!isSameWithConfirmPassword) {
      throw new ApplicationException(ErrorCode.NOT_SAME_WITH_CONFIRM, "비밀번호가 다릅니다.");
    }

    String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
    Member member = new Member(memberSequence, signupRequestDto.getEmail(), encodedPassword,
      signupRequestDto.getNickname(), signupRequestDto.getProfileImage());
    memberRepository.addMember(member);
  }

//...
}
```
이번 리팩토링을 통해 `MemberService`와 `MemberRepositroyImpl`를 SRP 관점에서 개선할 수 있었습니다.

`MemberRepositoryImpl`은 더이상 비밀번호의 인코딩과 유효성 검사 및 DTO <-> Entity 변환 로직을 진행하지 않고 오로지 `Member` 엔티티와 관련된 로직만 수행하면 됩니다.

해당 로직들은 모두 `MemberService`에서 수행하게끔 했으며, 비즈니스 로직을 맡은 Service 계층과 DB <-> Entity 매핑을 맡은 Repository 계층을 더욱 명확히 분리함으로써 유지보수성과 재사용성을 개선하였습니다.
