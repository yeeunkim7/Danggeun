package org.example.danggeun.user.dto;

import lombok.Data;
import org.example.danggeun.user.validation.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
public class UserRegisterRequestDto {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    private String email;

    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 2, max = 50, message = "사용자명은 2자 이상 50자 이하로 입력해주세요.")
    private String username;

    @ValidPassword
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String confirmPassword;

    @Pattern(regexp = "^01[016789]-?[0-9]{3,4}-?[0-9]{4}$",
            message = "올바른 휴대폰 번호 형식이 아닙니다.")
    private String phone;
}