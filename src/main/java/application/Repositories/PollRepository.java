package application.Repositories;

import application.CompositeKeys.PollID;
import application.Modals.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll, PollID> {
}
