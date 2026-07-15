package org.jedi_bachelor.bookstatistic.notificationservice.repository;

import org.jedi_bachelor.bookstatistic.notificationservice.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationSettingsRepository
        extends JpaRepository<NotificationSettings, UUID> {
    Optional<NotificationSettings> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("SELECT ns.email FROM NotificationSettings ns WHERE ns.enableGettingBroadcastMessages = :enable")
    List<String> findEmailsByEnableGettingBroadcastMessages(@Param("enable") Boolean enable);
}
