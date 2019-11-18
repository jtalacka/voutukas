package application.Repositories;

import application.CompositeKeys.PollID;
import application.Modals.Option;
import application.Modals.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, PollID> {

}
