package application.Repositories;

import application.domain.PollID;
import application.domain.Poll;
import application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, PollID> {

    @Query("select o from Poll o where o.owner = ?1")
    List<Poll> findPollByUser(User user);

    List<Poll> findByIdChannelId(String channelId);

    @Query("select p from Poll p where p.owner = ?1")
    List<Poll> selectAllPollsByUser(User user);

}
