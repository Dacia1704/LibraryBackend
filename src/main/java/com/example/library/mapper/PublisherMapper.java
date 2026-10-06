package com.example.library.mapper;

import com.example.library.dto.book_info.request.PublisherRequest;
import com.example.library.dto.book_info.response.PublisherResponse;
import com.example.library.entity.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PublisherMapper {

    PublisherResponse toPublisherResponse(Publisher publisher);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "noAccent", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "books", ignore = true)
    Publisher toPublisher(PublisherRequest request);
}