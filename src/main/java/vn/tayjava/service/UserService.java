package vn.tayjava.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import vn.tayjava.common.UserStatus;
import vn.tayjava.dto.request.UserChangePasswordRequest;
import vn.tayjava.dto.request.UserCreationRequest;
import vn.tayjava.dto.request.UserUpdateRequest;
import vn.tayjava.dto.response.PageResponse;
import vn.tayjava.dto.response.UserDetailResponse;

public interface UserService {

//    UserDetailsService userDetailsService();

    long saveUser(UserCreationRequest request);

    void updateUser(UserUpdateRequest request);

    void changeStatus(Long userId, UserStatus status);

    void changePassword(UserChangePasswordRequest request);

    void deleteUser(Long userId);

    UserDetailResponse getUserDetail(Long userId);

    PageResponse<?> getAllUserWithBySort(int pageNo, int pageSize, String sortBy);
}
