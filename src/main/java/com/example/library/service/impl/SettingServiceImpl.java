package com.example.library.service.impl;

import com.example.library.dto.book.request.SettingRequest;
import com.example.library.dto.book.response.SettingResponse;
import com.example.library.entity.Setting;
import com.example.library.mapper.SettingMapper;
import com.example.library.repository.SettingRepository;
import com.example.library.service.SettingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingServiceImpl implements SettingService {

    SettingRepository settingRepository;
    SettingMapper settingMapper;

    @Override
    @Transactional
    public SettingResponse create(SettingRequest request) {

        if (settingRepository.existsBySettingKeyAndIsDeletedFalse(
                request.getSettingKey()
        )) {
            throw new IllegalArgumentException(
                    "Setting key already exists"
            );
        }

        Setting setting = settingMapper.toSetting(request);

        setting.setIsDeleted(false);
        setting.setUpdatedAt(LocalDateTime.now());

        setting = settingRepository.save(setting);

        return settingMapper.toResponse(setting);
    }

    @Override
    @Transactional
    public SettingResponse update(
            Long id,
            SettingRequest request
    ) {

        Setting setting = settingRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Setting not found"
                        ));

        if (settingRepository
                .existsBySettingKeyAndIdNotAndIsDeletedFalse(
                        request.getSettingKey(),
                        id
                )) {

            throw new IllegalArgumentException(
                    "Setting key already exists"
            );
        }

        settingMapper.updateSetting(setting, request);

        setting.setUpdatedAt(LocalDateTime.now());

        setting = settingRepository.save(setting);

        return settingMapper.toResponse(setting);
    }

    @Override
    @Transactional
    public SettingResponse delete(Long id) {

        Setting setting = settingRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Setting not found"
                        ));

        setting.setIsDeleted(true);

        setting.setUpdatedAt(LocalDateTime.now());

        setting = settingRepository.save(setting);

        return settingMapper.toResponse(setting);
    }

    @Override
    @Transactional(readOnly = true)
    public SettingResponse getById(Long id) {

        Setting setting = settingRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Setting not found"
                        ));

        return settingMapper.toResponse(setting);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SettingResponse> getAll() {

        return settingRepository.findAllByIsDeletedFalse()
                .stream()
                .map(settingMapper::toResponse)
                .toList();
    }
}