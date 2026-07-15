package org.jedi_bachelor.bookstatistic.bookservice.audit;

import org.hibernate.envers.RevisionListener;
import org.jedi_bachelor.bookstatistic.bookservice.audit.entity.AuditRevisionEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuditRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity entity = (AuditRevisionEntity) revisionEntity;

        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //entity.setUsername(auth != null ? auth.getName() : "anonymous");
        entity.setUsername("anonymous");
        entity.setUserAgent(this.getUserAgent());
    }

    private String getUserAgent() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            return attributes.getRequest().getHeader("User-Agent");
        }

        return "unknown";
    }
}
