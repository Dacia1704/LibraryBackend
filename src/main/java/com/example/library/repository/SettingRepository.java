package com.example.library.repository;

import com.example.library.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {

    List<Setting> findAllByIsDeletedFalse();

    Optional<Setting> findByIdAndIsDeletedFalse(Long id);

    Optional<Setting> findBySettingKeyAndIsDeletedFalse(String settingKey);

    boolean existsBySettingKeyAndIsDeletedFalse(String settingKey);

    boolean existsBySettingKeyAndIdNotAndIsDeletedFalse(
            String settingKey,
            Long id
    );
}