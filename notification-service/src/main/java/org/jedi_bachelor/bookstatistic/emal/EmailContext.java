package org.jedi_bachelor.bookstatistic.emal;

import lombok.Data;

@Data
public class EmailContext {
    private String to;
    private String subject;
    private String message;
}
