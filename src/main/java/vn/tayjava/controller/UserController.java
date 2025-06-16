package vn.tayjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.tayjava.dto.request.UserChangePasswordRequest;
import vn.tayjava.dto.request.UserChangeStatusRequest;
import vn.tayjava.dto.request.UserCreationRequest;
import vn.tayjava.dto.request.UserUpdateRequest;
import vn.tayjava.dto.response.ApiResponseSuccess;
import vn.tayjava.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Controller")
@Slf4j(topic = "USER-CONTROLLER")
public class UserController {
    private final UserService userService;

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping()
    public ApiResponseSuccess<?> addUser(@Valid @RequestBody UserCreationRequest request) {
        log.info("Add user request: {}", request);
        return new ApiResponseSuccess<>(HttpStatus.OK.value(), "User added successfully", userService.saveUser(request));
    }

    @Operation(method = "PUT", summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/udp")
    public ApiResponseSuccess<?> updateUser(@Valid @RequestBody UserUpdateRequest request) {
        log.info("Update user request: {}", request);
        userService.updateUser(request);
        return new ApiResponseSuccess<>(HttpStatus.ACCEPTED.value(), "User updated successfully");
    }

    @Operation(method = "PATCH", summary = "Change status", description = "Send a request via this API to change user status")
    @PatchMapping("/change-status")
    public ApiResponseSuccess<?> changeStatus(@Valid @RequestBody UserChangeStatusRequest request) {
        log.info("Change status request: {}", request);
        userService.changeStatus(request.getUserId(), request.getStatus());
        return new ApiResponseSuccess<>(HttpStatus.ACCEPTED.value(), "User status changed successfully");
    }

    @Operation(method = "PATCH", summary = "Change password", description = "Send a request via this API to change password")
    @PatchMapping("/change-password")
    public ApiResponseSuccess<?> changePassword(@Valid @RequestBody UserChangePasswordRequest request) {
        log.info("Change password request: {}", request);
        userService.changePassword(request);
        return new ApiResponseSuccess<>(HttpStatus.ACCEPTED.value(), "User password changed successfully");
    }

    @Operation(method = "DELETE", summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ApiResponseSuccess<?> deleteUserPermanently(@PathVariable Long userId) {
        log.info("Delete user permanently request: {}", userId);
        userService.deleteUser(userId);
        return new ApiResponseSuccess<>(HttpStatus.ACCEPTED.value(), "User permanently deleted successfully");
    }

    @Operation(method = "GET", summary = "Get user detail", description = "Send a request via this API to get user detail by userId")
    @GetMapping("/{userId}")
    public ApiResponseSuccess<?> getUserDetail(@PathVariable Long userId) {
        log.info("Get user detail request: {}", userId);
        return new ApiResponseSuccess<>(HttpStatus.OK.value(), "User detail retrieved successfully", userService.getUserDetail(userId));
    }

    @Operation(method = "GET", summary = "Get  list of users per pageNo", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple columns")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ApiResponseSuccess<?> getAllUsersWithSortByMultipleColumns(@RequestParam(defaultValue = "0") int pageNo,
                                                                      @RequestParam(defaultValue = "20") int pageSize,
                                                                      @RequestParam(required = false) String sortBy) {
        log.info("Request get all of users with sort by multiple columns");
        return new ApiResponseSuccess<>(HttpStatus.OK.value(), "users", userService.getAllUserWithBySort(pageNo, pageSize, sortBy));
    }


}
