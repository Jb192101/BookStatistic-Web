package org.jedi_bachelor.bookstatistic.notificationservice.repository;

import org.jedi_bachelor.bookstatistic.notificationservice.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationSettingsRepository
        extends JpaRepository<NotificationSettings, UUID> {
    Optional<NotificationSettings> findByUserId(UUID userId);

    List<String> findByEnableGettingBroadcastMessages(Boolean enable);
}
