package application.Repositories;

import application.domain.Option;
import application.domain.Poll;
import application.domain.Properties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PropertiesRepository extends JpaRepository<Properties, Integer> {
}
