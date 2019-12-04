package application.Repositories;


import application.domain.Option;
import application.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {

    @Query("SELECT o FROM Option o WHERE o.poll = ?1")
    List<Option> findAllOptionsByPollID(Poll PollID);

    Option findFirstByOrderByIdDesc();

    @Query("SELECT o FROM Option o WHERE o.poll = ?1 AND o.optionText = ?2")
    Option findPollOptionsByPollIdAndOptionText(Poll poll, String optionText);

    @Modifying
    @Query("DELETE FROM Option o WHERE o.poll = ?1 AND o.optionText = ?2")
    void deletePollOptionsByPollIdAndOptionText(Poll poll, String optionText);

    @Modifying
    @Query("DELETE FROM Option o WHERE o.poll = ?1")
    void deletePollOptionsByPollID(Poll poll);
}
