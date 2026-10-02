package com.example.library.mapper;

import com.example.library.dto.book.request.SettingRequest;
import com.example.library.dto.book.response.SettingResponse;
import com.example.library.entity.Setting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SettingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Setting toSetting(SettingRequest request);

    SettingResponse toResponse(Setting setting);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateSetting(
            @MappingTarget Setting setting,
            SettingRequest request
    );
}