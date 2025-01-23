package org.example.repository;

import lombok.extern.log4j.Log4j2;
import org.example.entities.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Repository
public class UserDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(User.class, id));
    }

    public Optional<User> getUserByLogin(String login) {
        log.info("Searching for User with login: {}", login);
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();
    }

    public User createUser(User user) {
        log.info("Saving User: {}", user);

        sessionFactory.getCurrentSession().persist(user);
        return user;
    }
}
