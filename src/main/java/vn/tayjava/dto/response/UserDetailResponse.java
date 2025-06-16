package vn.tayjava.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.tayjava.common.Gender;
import vn.tayjava.common.UserStatus;
import vn.tayjava.common.UserType;

import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Builder
public class UserDetailResponse {
    private long id;
    private String fullName;
    private Gender gender;
    private Date dateOfBirth;
    private String email;
    private String phoneNumber;
    private String username;
    private UserStatus status;
    private UserType type;
    private Set<AddressDetailResponse> addresses;
}
