package org.jedi_bachelor.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jedi_bachelor.dto.RegistrationRequest;
import org.jedi_bachelor.email.EmailService;
import org.jedi_bachelor.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import org.jedi_bachelor.model.entities.Account;

@Component
@RequiredArgsConstructor
@Aspect
public class EmailAspect {
    @Autowired
    private final EmailService emailService;
    @Autowired
    private final AccountRepository accountRepository;

    @Pointcut("@annotation(org.jedi_bachelor.aop.SendEmailMessage) && args(userForm..)")
    public void emailSendingPointToUserForm() {}

    @Pointcut("@annotation(org.jedi_bachelor.aop.SendEmailMessage) && args(account..)")
    public void emailSendingPointToAccount() {}

    @After("emailSendingPointToUserForm()")
    public void sendEmailMessage(ProceedingJoinPoint joinPoint, RegistrationRequest userForm) {
        SendEmailMessage sendEmailMessage = getEmailSendingAnnotation(joinPoint);
        if(sendEmailMessage == null)
            return;

        long idOfMessage = sendEmailMessage.idOfMessage();

        if (idOfMessage == 0L) {
            emailService.sendEmail(userForm.getEmail(), "Вы успешно зарегистрировались!", "some text");
        } else if (idOfMessage == 1L) {
        }
    }

    /*
    * Метод для отправки сообщений пользователю, который уже зарегистрирован
    * @param joinPoint (ProceedingJoinPoint) - точка входа
    * @param sendEmailMessage (SendEmailMessage) - аннотация отправки сообщения
     */
    @After("@annotation(sendEmailMessage)")
    public void sendEmailToCurrentUser(ProceedingJoinPoint joinPoint,
                                         SendEmailMessage sendEmailMessage) throws Throwable {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = null;

        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            String username = authentication.getName();
            userEmail = accountRepository.findByUsername(username)
                    .map(Account::getEmail)
                    .orElse(null);
        }

        if (userEmail == null) {
            System.err.println("Cannot determine user email for notification");
        }

        long idOfMessage = sendEmailMessage.idOfMessage();
        emailService.sendEmail(userEmail, idOfMessage);
    }

    private SendEmailMessage getEmailSendingAnnotation(ProceedingJoinPoint joinPoint) {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            return method.getAnnotation(SendEmailMessage.class);
        } catch (Exception e) {
            return null;
        }
    }
}
