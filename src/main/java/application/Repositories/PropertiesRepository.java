package application.Repositories;

import application.domain.Properties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertiesRepository extends JpaRepository<Properties, Integer> {
    @Query("SELECT p FROM Properties p WHERE p.name = ?1")
    Properties findProperty(String name);
}
