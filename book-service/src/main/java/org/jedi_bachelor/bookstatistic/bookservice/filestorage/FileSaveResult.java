package org.jedi_bachelor.bookstatistic.bookservice.filestorage;

public record FileSaveResult(
        String path, String originalName, long size, String contentType
) {}
