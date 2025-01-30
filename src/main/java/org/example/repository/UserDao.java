package org.example.repository;

import lombok.extern.log4j.Log4j2;
import org.example.entities.User;
import org.example.exceptions.EntityAlreadyExistsException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
        log.info("Fetching user by ID: {}", id);
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(User.class, id));
    }

    public Optional<User> getUserByLogin(String login) {
        log.info("Fetching user by login: {}", login);
        Session session = sessionFactory.getCurrentSession();

        Optional<User> user = session.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", login)
                .uniqueResultOptional();

        if (user.isPresent()) {
            log.info("User found with login: {}", login);
        } else {
            log.warn("No user found with login: {}", login);
        }

        return user;
    }

    public User createUser(User user) {
        log.info("Saving User: {}", user);

        try {
            sessionFactory.getCurrentSession().persist(user);
            log.info("User successfully saved: {}", user);

        } catch (ConstraintViolationException e) {
            throw new EntityAlreadyExistsException("User with login '" + user.getLogin() + "' already exists", e);
        }

        return user;
    }
}
