package org.example.repository;

import lombok.extern.log4j.Log4j2;
import org.example.entities.Location;
import org.example.exceptions.EntityAlreadyExistsException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Log4j2
@Repository
public class LocationDao {

    private final SessionFactory sessionFactory;

    @Autowired
    public LocationDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Location createLocation(Location location) {
        log.info("Saving Location: {}", location);

        try {
            sessionFactory.getCurrentSession().persist(location);

        } catch (ConstraintViolationException e) {
            throw new EntityAlreadyExistsException("Location '" + location.getName() + "' for user '" + location.getUser().getLogin() + "' already exists", e);
        }

        return location;
    }

    public List<Location> getLocationsByUserId(int userId) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery(
                        "SELECT l FROM Location l WHERE l.user.id = :userId",
                        Location.class)
                .setParameter("userId", userId)
                .list();
    }

    public void deleteLocationByNameForUser(String locationName, int userId) {
        Session session = sessionFactory.getCurrentSession();

        Optional<Location> locationToDelete = session.createQuery(
                        "SELECT l FROM Location l " +
                                "WHERE l.user.id = :userId " +
                                "AND l.name = :locationName", Location.class)
                .setParameter("userId", userId)
                .setParameter("locationName", locationName)
                .uniqueResultOptional();

        if (locationToDelete.isPresent()) {
            session.remove(locationToDelete.get());
        }
    }
}
