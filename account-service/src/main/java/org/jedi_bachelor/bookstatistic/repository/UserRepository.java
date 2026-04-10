package org.jedi_bachelor.bookstatistic.repository;

import org.jedi_bachelor.bookstatistic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
