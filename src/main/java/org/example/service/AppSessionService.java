package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.AppSession;
import org.example.entities.User;
import org.example.entities.dto.UserDto;
import org.example.entities.mappers.UserDtoToUserMapper;
import org.example.exceptions.SessionNotFoundException;
import org.example.exceptions.UserNotFoundException;
import org.example.repository.AppSessionDao;
import org.example.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AppSessionService {

    @Value("${session.duration.seconds}")
    private long sessionDurationSeconds;

    private final AppSessionDao appSessionDao;
    private final UserDao userDao;

    private final UserDtoToUserMapper userDtoToUserMapper = UserDtoToUserMapper.INSTANCE;

    @Autowired
    public AppSessionService(AppSessionDao appSessionDao, UserDao userDao) {
        this.appSessionDao = appSessionDao;
        this.userDao = userDao;
    }

    @Transactional
    public String createSession(int userId) {
        User user = userDao.getUserById(userId).orElseThrow(() ->
                new UserNotFoundException("User with id = " + userId + " was not found in DataBase"));

        String sessionId = UUID.randomUUID().toString();

        AppSession appSession = AppSession.builder()
                .id(sessionId)
                .user(user)
                .expiresAt(ZonedDateTime.now(ZoneId.of("UTC")).plusSeconds(sessionDurationSeconds))
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
    public UserDto getUserDtoBySessionId(String sessionId) {
        log.info("Getting UserId by SessionId: {}", sessionId);

        AppSession appSession = appSessionDao.findById(sessionId).orElseThrow(() ->
                new SessionNotFoundException("Session with id " + sessionId + " was not found in Data Base"));

        User user = appSession.getUser();
        return userDtoToUserMapper.toDto(user);
    }

    @Transactional
    public void deleteSessionById(String sessionId) {
        log.info("Deleting session with id {}", sessionId);
        appSessionDao.deleteById(sessionId);
    }

    @Transactional
    @Scheduled(fixedRateString = "${session.cleanup.rate}") //prod = 1h; test = 1 min
    public void cleanExpiredSessions() {
        log.info("Deleting expired sessions by the Schedule");
        appSessionDao.deleteExpiredSessions(ZonedDateTime.now());
    }
}
