package org.jedi_bachelor.controller;

/*
* Контроллер для переходов
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {
    /*
    * Метод перехода на страницу регистрации при заходе на http://bookstatistic-web/
     */
    @GetMapping
    public String mainRedirect() {
        return "redirect:auth/login";
    }
}
