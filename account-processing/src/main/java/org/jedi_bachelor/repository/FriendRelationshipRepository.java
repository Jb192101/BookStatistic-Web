package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.model.entities.FriendRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRelationshipRepository extends JpaRepository<FriendRelationship, Long> {
    List<FriendRelationship> findFriendRelationshipByFriend1(Account account);
    List<FriendRelationship> findFriendRelationshipByFriend2(Account account);
    Boolean existsByFriend1AndFriend2(Account friend1, Account friend2);
}
