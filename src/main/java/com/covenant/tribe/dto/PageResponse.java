package com.covenant.tribe.dto;

import lombok.Value;

import java.util.List;

@Value
public class PageResponse<T> {

    List<T> content;
    Metadata metadata;

    public static class Metadata {
        int page;
        int size;
        long totalElements;
    }
}
