package org.jedi_bachelor.bookstatistic.emal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailContext {
    private String to;
    private String subject;
    private String message;
}
