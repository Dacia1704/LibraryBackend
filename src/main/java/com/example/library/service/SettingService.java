package com.example.library.service;


import com.example.library.dto.book.request.SettingRequest;
import com.example.library.dto.book.response.SettingResponse;
import com.example.library.entity.Setting;

import java.util.List;

public interface SettingService {

    SettingResponse create(SettingRequest request);

    SettingResponse update(Long id, SettingRequest request);

    SettingResponse delete(Long id);

    SettingResponse getById(Long id);

    List<SettingResponse> getAll();

    Setting getSetiingById(Long id);

}