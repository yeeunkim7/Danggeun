package org.example.danggeun.user.util;

import java.util.regex.Pattern;

public class PasswordUtils {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean isValid(String password) {
        return password != null && pattern.matcher(password).matches();
    }

    public static String getPasswordPolicy() {
        return "비밀번호는 영문, 숫자, 특수문자(@$!%*?&)를 포함하여 8~15자로 입력해주세요.";
    }
}