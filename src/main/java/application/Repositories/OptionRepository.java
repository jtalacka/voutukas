package application.Repositories;


import application.CompositeKeys.PollID;
import application.Modals.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {

    @Query("SELECT o FROM Option o WHERE o.pollId = ?1")
    List<Option> findAllOptionsByPollID(PollID PollID);
}
