package org.jedi_bachelor.bookstatistic.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public class EmailValidator {
    /**
     * Метод валидации одного email-адреса
     *
     * @param email строка
     * @return true, если строка является email-адресом
     */
    public static boolean validate(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    /**
     * Метод валидации коллекции строк на соответствие email-адресу
     *
     * @param emails коллекция строк
     * @return строки, соответствующие адресу email
     */
    public static Collection<String> validate(Collection<String> emails) {
        Collection<String> result = new ArrayList<>();

        for(String email : emails) {
            if(validate(email)) {
                result.add(email);
            }
        }

        return result;
    }
}
