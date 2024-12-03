package com.app.userService.user.domain.model;

import java.util.List;

public class PaginatedResult<T> {
  private final List<T> items;
  private final long totalItems;
  private final int totalPages;

  private PaginatedResult(List<T> items, long totalItems, int totalPages) {
    this.items = items;
    this.totalItems = totalItems;
    this.totalPages = totalPages;
  }

  public static <T> PaginatedResult<T> of(List<T> items, long totalItems, int totalPages) {
    return new PaginatedResult<>(items, totalItems, totalPages);
  }

  public List<T> getItems() {
    return items;
  }

  public long getTotalItems() {
    return totalItems;
  }

  public int getTotalPages() {
    return totalPages;
  }
}

