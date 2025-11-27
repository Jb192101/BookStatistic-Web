package org.jedi_bachelor.repository;

import org.jedi_bachelor.model.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
