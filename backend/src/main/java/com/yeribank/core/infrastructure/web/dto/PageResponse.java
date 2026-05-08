package com.yeribank.core.infrastructure.web.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages) {

  public static <T> PageResponse<T> of(List<T> items, int page, int size) {
    int safePage = Math.max(0, page);
    int safeSize = Math.max(1, Math.min(size, 100));
    int fromIndex = Math.min(safePage * safeSize, items.size());
    int toIndex = Math.min(fromIndex + safeSize, items.size());
    int totalPages = (int) Math.ceil((double) items.size() / safeSize);

    return new PageResponse<>(
        items.subList(fromIndex, toIndex),
        safePage,
        safeSize,
        items.size(),
        totalPages);
  }
}
