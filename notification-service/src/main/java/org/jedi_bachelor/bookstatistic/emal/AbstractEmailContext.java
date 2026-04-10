package org.jedi_bachelor.bookstatistic.emal;

import lombok.Data;

import java.util.Map;

@Data
abstract public class AbstractEmailContext {
    private String to;
    private String subject;
    private String attachment;
    private String fromDisplayName;
    private String displayName;
    private String templateLocation;
    private Map<String, Object> context;
}
