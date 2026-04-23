package org.jedi_bachelor.bookstatistic.repository;

import org.jedi_bachelor.bookstatistic.entity.Text;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TextRepository extends JpaRepository<Text, UUID> {
}
