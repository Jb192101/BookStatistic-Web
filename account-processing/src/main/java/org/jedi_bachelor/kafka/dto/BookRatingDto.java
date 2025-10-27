package org.jedi_bachelor.kafka.dto;

import lombok.Data;

@Data
public class BookRatingDto {
    private Long bookId;
    private Integer newReadedPages; // разность между ранее прочитанным и ныне прочитанным кол-вом страниц
}
