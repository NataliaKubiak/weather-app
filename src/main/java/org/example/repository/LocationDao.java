package org.example.repository;

import lombok.extern.log4j.Log4j2;
import org.example.entities.Location;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

        Session session = sessionFactory.getCurrentSession();
        Optional<Location> maybeExistedLocation = session.createQuery(
                        "SELECT l FROM Location l " +
                                "WHERE l.name = :locationName " +
                                "AND l.user.id = :userId", Location.class)
                .setParameter("locationName", location.getName())
                .setParameter("userId", location.getUser().getId())
                .uniqueResultOptional();

        if (maybeExistedLocation.isEmpty()) {
            session.persist(location);
            return location;
        } else {
            return maybeExistedLocation.get();
        }
    }

    public List<Location> getLocationsByUserId(int userId) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery(
                        "SELECT l FROM Location l WHERE l.user.id = :userId",
                        Location.class)
                .setParameter("userId", userId)
                .list();
    }

    public void deleteLocationForUser(String locationName, int userId) {
        Session session = sessionFactory.getCurrentSession();

        Optional<Location> locationToDelete = session.createQuery(
                        "SELECT l FROM Location l " +
                                "WHERE l.name = :locationName " +
                                "AND l.user.id = :userId", Location.class)
                .setParameter("locationName", locationName)
                .setParameter("userId", userId)
                .uniqueResultOptional();

        locationToDelete.ifPresent(session::remove);
    }
}
