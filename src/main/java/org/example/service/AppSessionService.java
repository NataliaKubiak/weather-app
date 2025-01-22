package org.example.service;

import org.example.entities.AppSession;
import org.example.entities.User;
import org.example.repository.AppSessionDao;
import org.example.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppSessionService {

    private static final long SESSION_DURATION = 2L;

    private final AppSessionDao appSessionDao;
    private final UserDao userDao;

    @Autowired
    public AppSessionService(AppSessionDao appSessionDao, UserDao userDao) {
        this.appSessionDao = appSessionDao;
        this.userDao = userDao;
    }

    @Transactional
    public String createSession(int userId) {
        // TODO: 21/01/2025 заменить RuntimeException на что-то meaningful
        User user = userDao.getUserById(userId).orElseThrow(() -> new RuntimeException());

        String sessionId = UUID.randomUUID().toString();

        AppSession appSession = AppSession.builder()
                .id(sessionId)
                .user(user)
                .expiresAt(ZonedDateTime.now(ZoneId.of("UTC")).plusHours(SESSION_DURATION))
                .build();

        appSessionDao.createSession(appSession);
        return sessionId;
    }

    @Transactional
    public boolean isValidSession(String  sessionId) {
        Optional<AppSession> maybeSession = appSessionDao.findById(sessionId);

        return maybeSession.isPresent() &&
                maybeSession.get().getExpiresAt().isAfter(ZonedDateTime.now());
    }

    @Transactional
    public void deleteSessionById(String sessionId) {
        appSessionDao.deleteById(sessionId);
    }

    @Transactional
    @Scheduled(fixedRate = 3600000) //every hour
//    @Scheduled(fixedRate = 120000) //every 2 min
    public void cleanExpiredSessions() {
        appSessionDao.deleteExpiredSessions(ZonedDateTime.now());
    }
}
