package org.jedi_bachelor.kafka.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BookRatingDto extends KafkaDtoMessage {
    private Long bookId;
    private Long userId;
    private Integer newReadedPages; // разность между ранее прочитанным и ныне прочитанным кол-вом страниц
    private Integer totalPages; // количество всех страниц в книге (для дальнейшего сравнения)
}
