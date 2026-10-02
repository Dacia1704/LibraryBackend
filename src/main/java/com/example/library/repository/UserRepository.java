package com.example.library.repository;

import java.util.List;
import java.util.Optional;

import com.example.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    boolean existsByUsernameAndIsDeletedFalse(String username);

    boolean existsByEmailAndIsDeletedFalse(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByIsDeletedFalse();

    Optional<User> findByIdAndIsDeletedFalse(Long id);
}
