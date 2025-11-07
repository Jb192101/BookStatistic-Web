package org.jedi_bachelor.service;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.model.entities.Achievement;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final List<Achievement> achievements;
}
