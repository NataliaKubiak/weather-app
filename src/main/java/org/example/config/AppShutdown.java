package org.example.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import liquibase.Scope;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

@Log4j2
@Component
public class AppShutdown {

    // этот метод убрал этот WARNING [main] org.apache.catalina.loader.WebappClassLoaderBase.clearReferencesJdbc The web application [ROOT] registered the JDBC driver [org.postgresql.Driver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
    @PreDestroy
    public void driversCleanup() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            try {
                Driver driver = drivers.nextElement();
                DriverManager.deregisterDriver(driver);
                log.info("Deregistered JDBC driver: {}", driver);

            } catch (Exception e) {
                log.error("Error deregistering driver: {}", String.valueOf(e));
            }
        }
    }
}
