package vn.tayjava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.tayjava.common.UserStatus;
import vn.tayjava.common.UserType;
import vn.tayjava.dto.request.UserChangePasswordRequest;
import vn.tayjava.dto.request.UserCreationRequest;
import vn.tayjava.dto.request.UserUpdateRequest;
import vn.tayjava.dto.response.AddressDetailResponse;
import vn.tayjava.dto.response.PageResponse;
import vn.tayjava.dto.response.UserDetailResponse;
import vn.tayjava.exception.ResourceNotFoundException;
import vn.tayjava.model.AddressEntity;
import vn.tayjava.model.UserEntity;
import vn.tayjava.repository.AddressRepository;
import vn.tayjava.repository.UserRepository;
import vn.tayjava.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "USER-SERVICE")
public class UserServiceImpl implements UserService {
    private static final String SORT_BY_REGEX = "([a-zA-Z]+):(asc|desc)";
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

//    @Override
//    public UserDetailsService userDetailsService() {
//        return username -> userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long saveUser(UserCreationRequest request) {
        log.info("Saving user {}", request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateKeyException("This email address already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateKeyException("This username already exists");
        }

        UserEntity user = UserEntity.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .type(UserType.USER)
                .status(UserStatus.NONE)
                .build();
        request.getAddresses().forEach(address -> user.saveAddress(AddressEntity.builder()
                .apartmentNumber(address.getApartmentNumber())
                .floor(address.getFloor())
                .building(address.getBuilding())
                .streetNumber(address.getStreetNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .country(address.getCountry())
                .addressType(address.getAddressType())
                .build()));
        userRepository.save(user);
        log.info("User {} saved", user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequest request) {
        log.info("Updating user {}", request);
        UserEntity user = getUserById(request.getId());
        user.setFullName(request.getFullName());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("User updated");

        Set<AddressEntity> addresses = new HashSet<>();
        request.getAddresses().forEach(address -> {
            AddressEntity addressEntity = addressRepository.findByUserIdAndAddressType(request.getId(), address.getAddressType());
            if (addressEntity == null) {
                addressEntity = new AddressEntity();
            }
            addressEntity.setApartmentNumber(address.getApartmentNumber());
            addressEntity.setFloor(address.getFloor());
            addressEntity.setBuilding(address.getBuilding());
            addressEntity.setStreetNumber(address.getStreetNumber());
            addressEntity.setStreet(address.getStreet());
            addressEntity.setCity(address.getCity());
            addressEntity.setCountry(address.getCountry());
            addressEntity.setAddressType(address.getAddressType());
            addressEntity.setUser(user);
            addresses.add(addressEntity);
        });
        addressRepository.saveAll(addresses);
        log.info("Updated {} addresses", addresses.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long userId, UserStatus status) {
        log.info("Changing status of user {}", userId);
        UserEntity user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("Status changed to {}", status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserChangePasswordRequest request) {
        log.info("Changing password {}", request);
        UserEntity user = getUserById(request.getUserId());
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully");
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long userId) {
        log.info("Retrieving user details for {}", userId);

        UserEntity user = getUserById(userId);

        Set<AddressDetailResponse> addresses = new HashSet<>();
        user.getAddresses().forEach(address -> {
            AddressDetailResponse addressDetailResponse = AddressDetailResponse.builder()
                    .apartmentNumber(address.getApartmentNumber())
                    .floor(address.getFloor())
                    .building(address.getBuilding())
                    .streetNumber(address.getStreetNumber())
                    .street(address.getStreet())
                    .city(address.getCity())
                    .country(address.getCountry())
                    .addressType(address.getAddressType())
                    .build();
            addresses.add(addressDetailResponse);
        });

        return UserDetailResponse.builder()
                .fullName(user.getFullName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .username(user.getUsername())
                .type(user.getType())
                .status(user.getStatus())
                .addresses(addresses)
                .build();
    }

    @Override
    public PageResponse<?> getAllUserWithBySort(int pageNo, int pageSize, String sortBy) {
        int page = Math.max(pageNo - 1, 0);

        List<Sort.Order> orders = new ArrayList<>();
        if(StringUtils.hasLength(sortBy)) {
            String[] sortByArray = sortBy.split(",");
            Pattern pattern = Pattern.compile(SORT_BY_REGEX);
            for (String s : sortByArray) {
                Matcher matcher = pattern.matcher(s);
                if(matcher.matches()) {
                    String property = matcher.group(1);
                    String direction = matcher.group(2);
                    Sort.Direction directionEnum = direction.equalsIgnoreCase("asc")
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;
                    orders.add(new Sort.Order(directionEnum, property));
                }
            }
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(orders));
        List<UserEntity> users = userRepository.findAll(pageable).getContent();
        List<UserDetailResponse> userDetailResponses = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .username(user.getUsername())
                .type(user.getType())
                .status(user.getStatus())
                .addresses(user.getAddresses().stream().map(UserServiceImpl::convertToAddressDetailResponse).collect(Collectors.toSet()))
                .build()).toList();

        return PageResponse.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalElements(userDetailResponses.size())
                .items(userDetailResponses)
                .build();

    }

    public UserEntity getUserById(long userId) {
        return userRepository.findByIdWithAddresses(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }


    private static AddressDetailResponse convertToAddressDetailResponse(AddressEntity addressEntity) {
        return AddressDetailResponse.builder()
                .apartmentNumber(addressEntity.getApartmentNumber())
                .floor(addressEntity.getFloor())
                .building(addressEntity.getBuilding())
                .streetNumber(addressEntity.getStreetNumber())
                .street(addressEntity.getStreet())
                .city(addressEntity.getCity())
                .country(addressEntity.getCountry())
                .addressType(addressEntity.getAddressType())
                .build();
    }


}
