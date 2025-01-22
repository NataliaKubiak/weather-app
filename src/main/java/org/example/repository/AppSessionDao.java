package org.example.repository;

import lombok.extern.log4j.Log4j2;
import org.example.entities.AppSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Log4j2
@Repository
public class AppSessionDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public AppSessionDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<AppSession> findById(String sessionId) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(AppSession.class, sessionId));
    }

    public AppSession createSession(AppSession appSession) {
        log.info("Saving Session: {}", appSession);

        sessionFactory.getCurrentSession().persist(appSession);
        return appSession;
    }

    public void deleteById(String id) {
        log.info("Deleting Session with id: {}", id);
        Session session = sessionFactory.getCurrentSession();

        AppSession appSession = session.get(AppSession.class, id);

        if(appSession != null) {
            session.remove(appSession);
        }
    }

    public void deleteExpiredSessions(ZonedDateTime currentTime) {
        String query = "DELETE FROM AppSession s WHERE s.expiresAt <= :currentTime";

        sessionFactory.getCurrentSession()
                .createQuery(query)
                .setParameter("currentTime", currentTime)
                .executeUpdate();
    }
}
