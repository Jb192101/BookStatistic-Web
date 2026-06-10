package org.jedi_bachelor.bookstatistic.notificationservice.inbox.repository;

import org.jedi_bachelor.bookstatistic.notificationservice.inbox.entity.InboxNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxNotificationRepository extends JpaRepository<InboxNotificationEntity, Long> {
}
