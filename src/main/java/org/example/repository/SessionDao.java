package org.example.repository;

import lombok.extern.log4j.Log4j2;
import org.example.entities.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Log4j2
@Repository
public class SessionDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public SessionDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<Session> findById(String sessionId) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(Session.class, sessionId));
    }

    public Session createSession(Session session) {
        log.debug("Saving Session: {}", session);

        sessionFactory.getCurrentSession().persist(session);
        return session;
    }
}
