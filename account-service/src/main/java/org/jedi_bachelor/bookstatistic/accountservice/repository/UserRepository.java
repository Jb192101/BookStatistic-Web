package org.jedi_bachelor.bookstatistic.accountservice.repository;

import org.jedi_bachelor.bookstatistic.accountservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUsername(String username);
}
