package application.Repositories;

import application.domain.Poll;
import application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Integer> {

    @Query("select o from Poll o where o.owner = ?1 order by o.id asc")
    List<Poll> findPollByUser(User user);

    List<Poll> findByChannelIdOrderByIdAsc(String channelId);

    Poll findByChannelIdAndTimeStampOrderByIdAsc(String channelId, String timeStamp);

    @Modifying
    void deleteByChannelIdAndTimeStamp(String channelId,String timeStamp);

    @Query("select p from Poll p where p.owner = ?1 order by p.id asc")
    List<Poll> selectAllPollsByUser(User user);

    @Query(value = "select p.id from Poll p where p.timeStamp = ?1 and p.channelId = ?2")
    Integer getId(String timeStamp, String channelId);

}
