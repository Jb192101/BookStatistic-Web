package org.jedi_bachelor.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.aop.SendEmailMessage;
import org.jedi_bachelor.dto.RegistrationRequest;
import org.jedi_bachelor.model.entities.Account;
import org.jedi_bachelor.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthorityService authorityService;
    @Autowired
    private final AuthenticationManager authenticationManager;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new RegistrationRequest());

        return "account/register";
    }

    @PostMapping("/registration")
    @SendEmailMessage(idOfMessage = 0)
    public String addUser(@ModelAttribute("userForm") @Valid RegistrationRequest userForm,
                                     BindingResult bindingResult, Model model, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "account/register";
        }

        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            return "account/register";
        }

        Account account = new Account(userForm.getUsername(), userForm.getPassword(),
                userForm.getEmail(), "ROLE_SIMPLE_USER");

        if (!authorityService.saveUser(account)) {
            return "account/register";
        }

        authenticateUserAndSetSession(userForm.getUsername(), userForm.getPassword(), request);

        return "redirect:/main";
    }

    /*
    * Метод для создания сессии и авторизации пользователя в системе
    * @param username (String) - имя пользователя
    * @param rawPassword (String) - "сырой" пароль
    * @param request (HttpServletRequest) - запрос
     */
    private void authenticateUserAndSetSession(String username, String rawPassword, HttpServletRequest request) {
        try {
            System.out.println("Starting authentication for user: " + username);

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(username, rawPassword);

            Authentication authentication = authenticationManager.authenticate(token);

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            SecurityContextHolder.setContext(securityContext);

            System.out.println("Authentication successful for user: " + username);
            System.out.println("Session ID: " + session.getId());
            System.out.println("Authorities: " + authentication.getAuthorities());

        } catch (Exception e) {
            System.out.println("Auto-authentication failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Authentication failed", e);
        }
    }
}
