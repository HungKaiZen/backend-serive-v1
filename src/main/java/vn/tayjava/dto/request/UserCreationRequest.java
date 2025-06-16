package vn.tayjava.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import vn.tayjava.common.Gender;
import vn.tayjava.dto.validation.ValidEnum;
import vn.tayjava.dto.validation.ValidPhoneNumber;

import java.util.Date;
import java.util.Set;

@Getter
public class UserCreationRequest {

    @NotBlank(message = "fullName must be not blank")
    private String fullName;

    @NotNull(message = "gender must be not null")
    @ValidEnum(name = "gender", regexp = "MALE|FEMALE|OTHER")
    private Gender gender;

    @NotNull(message = "dateOfBirth must be not null")
    @Past(message = "Date of birth must be before current date")
    private Date dateOfBirth;

    @NotNull(message = "email must be not null")
    @Pattern(regexp = "^(.+)@(\\S+)$", message = "Invalid email")
    private String email;

    @NotNull(message = "phoneNumber must be not null")
    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "username must be not blank")
    private String username;

    @NotBlank(message = "password must be not blank")
    private String password;

    @NotEmpty(message = "addresses must be not empty")
    private Set<AddressRequest> addresses;

}
