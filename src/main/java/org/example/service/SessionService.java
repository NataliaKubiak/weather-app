package org.example.service;

import org.example.entities.Session;
import org.example.entities.User;
import org.example.repository.SessionDao;
import org.example.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private static final long SESSION_DURATION = 2L;

    private final SessionDao sessionDao;
    private final UserDao userDao;

    @Autowired
    public SessionService(SessionDao sessionDao, UserDao userDao) {
        this.sessionDao = sessionDao;
        this.userDao = userDao;
    }

    @Transactional
    public String createSession(int userId) {
        // TODO: 21/01/2025 заменить RuntimeException на что-то meaningful
        User user = userDao.getUserById(userId).orElseThrow(() -> new RuntimeException());

        String sessionId = UUID.randomUUID().toString();

        Session session = Session.builder()
                .id(sessionId)
                .user(user)
                .expiresAt(ZonedDateTime.now().plusHours(SESSION_DURATION))
                .build();

        sessionDao.createSession(session);
        return sessionId;
    }

    @Transactional
    public boolean isValidSession(String  sessionId) {
        Optional<Session> maybeSession = sessionDao.findById(sessionId);

        return maybeSession.isPresent() &&
                maybeSession.get().getExpiresAt().isAfter(ZonedDateTime.now());
    }
}
