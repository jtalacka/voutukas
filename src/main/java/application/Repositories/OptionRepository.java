package application.Repositories;


import application.domain.Option;
import application.domain.Poll;
import application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {

    @Query("SELECT o FROM Option o WHERE o.pollId = ?1")
    List<Option> findAllOptionsByPollID(Poll PollID);

    Option findFirstByOrderByIdDesc();

    @Query("SELECT o FROM Option o WHERE o.pollId = ?1 AND o.optionText = ?2")
    Option findPollOptionsByPollIdAndOptionText(Poll poll, String optionText);

    @Query("DELETE FROM Option o WHERE o.pollId = ?1 AND o.optionText = ?2")
    void deletePollOptionsByPollIdAndOptionText(Poll poll, String optionText);
}
