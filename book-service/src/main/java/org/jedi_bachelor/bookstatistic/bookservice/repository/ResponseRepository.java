package org.jedi_bachelor.bookstatistic.bookservice.repository;

import org.jedi_bachelor.bookstatistic.bookservice.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<Response, Response.ResponseId> {
}
