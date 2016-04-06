package com.excilys.formation.exos.validation;

import com.excilys.formation.exos.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * User inputs validation
 */
public class Validator {

    /**
     * Validate an user name. It shouldn't be empty
     * @param user the user name
     */
    public static boolean validateUserName(String user) {
        if (user.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Validate an user password. It shouldn't be empty
     * @param pwd the user password
     */
    public static boolean validateUserPwd(String pwd) {
        if (pwd.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Validate the user credentials
     * @param user the user name
     * @param pwd the user password
     * @return the list containing the errors, the empty list otherwise
     */
    public static List<String> validateUserCredentials(String user, String pwd) {
        List<String> errors = new ArrayList<>(2);
        if (!validateUserName(user)) {
            errors.add(MainActivity.USER_ID);
        }
        if (!validateUserPwd(pwd)) {
            errors.add(MainActivity.PWD_ID);
        }
        return errors;
    }
}