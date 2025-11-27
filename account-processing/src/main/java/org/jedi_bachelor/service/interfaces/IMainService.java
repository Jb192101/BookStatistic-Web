package org.jedi_bachelor.service.interfaces;

import org.jedi_bachelor.model.entities.Account;

import java.util.Map;

public interface IMainService {
    Map<String, Object> calculateReadingProgress(Account user);
    String getFavoriteGenre(Account user);
}
