package relead.relead_schoolmanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import relead.relead_schoolmanagement.entities.LoginAttempt;
import relead.relead_schoolmanagement.repositories.LoginAttemptRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final LoginAttemptRepository repository;
    private final int MAX_ATTEMPTS = 5;
    private final int WINDOW_MINUTES = 1;

    public boolean isBlocked(String username, String ip) {
        Instant since = Instant.now().minus(WINDOW_MINUTES, ChronoUnit.MINUTES);
        long attemptsByUser = repository.countRecentByUsername(username, since);
        long attemptsByIp = repository.countRecentByIp(ip, since);
        return attemptsByUser >= MAX_ATTEMPTS || attemptsByIp >= MAX_ATTEMPTS;
    }

    public void recordFailedAttempt(String username, String ip) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUsername(username);
        attempt.setIpAddress(ip);
        attempt.setAttemptTime(Instant.now());
        repository.save(attempt);
    }

    public void clearAttempts(String username, String ip) {
        repository.deleteByUsernameAndIpAddress(username, ip);
    }
}

