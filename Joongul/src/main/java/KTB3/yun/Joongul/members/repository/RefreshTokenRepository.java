package KTB3.yun.Joongul.members.repository;

import KTB3.yun.Joongul.members.domain.RefreshToken;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshTokenAndRevokedFalse(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    @Nonnull
    @Override
    <S extends RefreshToken> S save(@Nonnull S entity);
}
