package org.jedi_bachelor.model.entities;

/*
Класс для отображения друзей на сайте
 */

import lombok.Data;

@Data
public class FriendAccount {
    private Long id;
    private String username;
    private Float rating;
    private String url;

    public void updateUrl() {
        this.url = "localhost:8081/" + "bookstatistic/accounts/" + id;
    }
}
