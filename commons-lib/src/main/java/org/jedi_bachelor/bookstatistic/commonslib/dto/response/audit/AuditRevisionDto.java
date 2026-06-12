package org.jedi_bachelor.bookstatistic.commonslib.dto.response.audit;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
public class AuditRevisionDto {
    private Long revisionId;

    private LocalDateTime revisionTimestamp;

    private String revisionType;  // ADD, MOD, DEL

    private String username;

    private String userAgent;

    private String entityId;

    private String entityType;

    private String entitySnapshot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditRevisionDto that)) return false;
        return Objects.equals(revisionTimestamp, that.revisionTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(revisionTimestamp);
    }
}
