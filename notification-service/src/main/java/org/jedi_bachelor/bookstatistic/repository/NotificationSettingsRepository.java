package org.jedi_bachelor.bookstatistic.repository;

import org.jedi_bachelor.bookstatistic.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationSettingsRepository
        extends JpaRepository<NotificationSettings, UUID> {
    NotificationSettings findByUserId(UUID userId);
}
