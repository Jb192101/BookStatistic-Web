package org.jedi_bachelor.kafka.dto;

import lombok.Data;

@Data
public class BookRatingDto extends KafkaDtoMessage {
    private Long bookId;
    private Long userId;
    private Integer newReadedPages; // разность между ранее прочитанным и ныне прочитанным кол-вом страниц
}
