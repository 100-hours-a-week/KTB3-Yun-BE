# 당신의 읽음이 당신의 세상을 - iLGum
> 독서 활동 기록 및 공유 커뮤니티 서비스 iLGum의 백엔드 Repository입니다.<p/>
> Spring Boot 및 MySQL를 기반으로 구현하였습니다.<p/>
> 사용자들은 본인이 읽은 책을 선택, 감상을 작성하고 다른 사용자와 공유할 수 있습니다.
# 개발 인원
|FE/BE (1인)|
|:-----:|
|yun.jeon(전윤철)|
# 기술 스택
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
# 주요 기능
|구분|기능|
|:-----:|:-----:|
|Auth|Spring Security + JWT 기반 인증/인가<br>토큰 재발급|
|Member|회원가입<br>회원정보 및 비밀번호 수정<br>회원 탈퇴|
|Post|게시글 CRUD|
|Comment|댓글 작성/수정 및 삭제|
|Like|게시글 좋아요 및 좋아요 취소|
# API 목록
|구분|기능|Method|Endpoint|
|:----:|:----:|:----:|:----:|
|Auth|토큰 재발급|POST|`/token`|
|Members|회원가입|POST|`/members`|
||로그인 회원 정보 조회|GET|`/members/me`|
||회원정보 수정|PUT|`/members/{id}`|
||비밀번호 수정|PATCH|`/members/{id}`|
||회원 탈퇴|DELETE|`/members/{id}`|
||로그인|POST|`/members/session`|
||로그아웃|POST|`/members/logout`|
|Posts|게시글 목록 조회|GET|`/posts`|
||게시글 단건 상세 조회|GET|`/posts/{id}`|
||게시글 작성|POST|`/posts`|
||게시글 수정|PUT|`/posts/{id}`|
||게시글 삭제|DELETE|`/posts/{id}`|
|Comments|댓글 작성|POST|`/posts/{postId}/comments`|
||댓글 수정|PUT|`/posts/{postId}/comments/{commentId}`|
||댓글 삭제|DELETE|`posts/{postId}/comments/{commentId}`|
|Likes|좋아요|POST|`/posts/{postId}/likes`|
||좋아요 취소|DELETE|`/posts/{postId}/likes`|
||좋아요 여부 확인|GET|`/posts/{postId}/likes`|
# ERD
<img width="1410" height="652" alt="KTB3_Week6_ERD(yun jeon)" src="https://github.com/user-attachments/assets/4e18b7b4-3bf6-491e-b7c3-a442e27bd5ae" />

# 화면 구성(❗️동영상으로 변경 예정입니다.)
<img width="1512" height="773" alt="로그인_화면" src="https://github.com/user-attachments/assets/a91cca3c-b132-498d-b4d3-b4f1903b2b7d" />
<img width="1512" height="773" alt="게시글_목록" src="https://github.com/user-attachments/assets/988d9df5-c16b-4771-ae44-b1c8cb5e3e9d" />
<img width="1512" height="773" alt="게시글_상세" src="https://github.com/user-attachments/assets/adebd06d-8d71-420b-88b0-fe3bc1416e78" />
<img width="1512" height="773" alt="게시글_작성" src="https://github.com/user-attachments/assets/af0ca9c5-064b-4584-b296-4485d6b7ab5e" />
<img width="1512" height="773" alt="마이페이지" src="https://github.com/user-attachments/assets/b0be1c8c-efc4-4933-a4c7-b2115eeb742b" />
