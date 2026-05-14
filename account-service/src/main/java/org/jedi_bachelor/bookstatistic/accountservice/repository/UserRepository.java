package org.jedi_bachelor.bookstatistic.accountservice.repository;

import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserProfile, UUID> {
}
