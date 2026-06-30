package org.jedi_bachelor.bookstatistic.bookservice.filestorage.repository;

import org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity.TextFile;
import org.springframework.data.repository.CrudRepository;

public interface TextFileRepository extends CrudRepository<TextFile, String> {
}
