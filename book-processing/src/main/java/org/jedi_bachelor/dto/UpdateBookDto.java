package org.jedi_bachelor.dto;

import lombok.Data;

@Data
public class UpdateBookDto {
    private Long bookId;
    private Long userId;
    private Integer newReadedPages; // кол-во прочитанных страниц
}
