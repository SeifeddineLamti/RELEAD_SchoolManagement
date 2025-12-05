package relead.relead_schoolmanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import relead.relead_schoolmanagement.entities.LoginAttempt;

import java.time.Instant;
import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    @Query("SELECT COUNT(l) FROM LoginAttempt l WHERE l.username = :username AND l.attemptTime > :since")
    long countRecentByUsername(@Param("username") String username, @Param("since") Instant since);

    @Query("SELECT COUNT(l) FROM LoginAttempt l WHERE l.ipAddress = :ip AND l.attemptTime > :since")
    long countRecentByIp(@Param("ip") String ip, @Param("since") Instant since);

    void deleteByUsernameAndIpAddress(String username, String ipAddress);
}

