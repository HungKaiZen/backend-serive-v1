package vn.tayjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.tayjava.model.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.addresses WHERE u.id = :userId")
    Optional<UserEntity> findByIdWithAddresses(@Param("userId") Long userId);


    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

}
