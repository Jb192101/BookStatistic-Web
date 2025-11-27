package org.jedi_bachelor.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    @Autowired
    private final AccountService accountService;

    @GetMapping
    public String mainPage(Model model,
                           HttpServletRequest request,
                           @RequestParam(value = "token", required = false) String token) {
        System.out.println("=== MAIN CONTROLLER CALLED ===");

        // Получаем аутентификацию из сессии
        HttpSession session = request.getSession(false);
        Authentication auth = null;

        if (session != null) {
            SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
            if (securityContext != null) {
                auth = securityContext.getAuthentication();
                System.out.println("Using authentication from session: " + auth);

                // УСТАНАВЛИВАЕМ КОНТЕКСТ БЕЗОПАСНОСТИ ИЗ СЕССИИ
                SecurityContextHolder.setContext(securityContext);
            }
        }

        // Если в сессии нет, пробуем из SecurityContextHolder
        if (auth == null) {
            auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Using authentication from SecurityContextHolder: " + auth);
        }

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            model.addAttribute("username", username);
            model.addAttribute("user", auth.getPrincipal());

            System.out.println("User " + username + " successfully authenticated for main page");

            // Заглушки для тестирования
            model.addAttribute("booksCount", 0);
            model.addAttribute("readingProgress", Map.of(
                    "completedBooks", 0,
                    "totalPages", 0,
                    "completionRate", 0,
                    "totalBooks", 0
            ));

            return "main";
        } else {
            System.out.println("User NOT authenticated - redirecting to login");
            return "redirect:/bookstatistic-web/login";
        }
    }
}
