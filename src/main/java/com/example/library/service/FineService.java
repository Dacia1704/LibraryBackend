package com.example.library.service;


import com.example.library.dto.book.response.FineResponse;

import java.util.List;

public interface FineService {

    List<FineResponse> getAll();

    List<FineResponse> getMe();
}