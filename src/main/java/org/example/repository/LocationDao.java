package org.example.repository;

import lombok.extern.log4j.Log4j2;
import org.example.entities.Location;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

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

        sessionFactory.getCurrentSession().persist(location);
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
}
