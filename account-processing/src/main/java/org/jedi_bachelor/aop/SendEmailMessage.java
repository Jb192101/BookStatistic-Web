package org.jedi_bachelor.aop;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SendEmailMessage {
    long idOfMessage() default 0L;
}
