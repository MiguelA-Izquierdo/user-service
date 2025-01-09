package com.app.userService.user.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaginatedResultTest {

  @Test
  void testPaginatedResultCreation() {
    List<String> items = Arrays.asList("item1", "item2", "item3");
    long totalItems = 10;
    int totalPages = 5;

    PaginatedResult<String> result = PaginatedResult.of(items, totalItems, totalPages);

    assertNotNull(result);
    assertEquals(items, result.getItems());
    assertEquals(totalItems, result.getTotalItems());
    assertEquals(totalPages, result.getTotalPages());
  }

  @Test
  void testEmptyResult() {
    List<String> items = List.of();
    long totalItems = 0;
    int totalPages = 0;

    PaginatedResult<String> result = PaginatedResult.of(items, totalItems, totalPages);

    assertNotNull(result);
    assertTrue(result.getItems().isEmpty());
    assertEquals(0, result.getTotalItems());
    assertEquals(0, result.getTotalPages());
  }

  @Test
  void testPaginationWithOneItem() {
    List<String> items = List.of("singleItem");
    long totalItems = 1;
    int totalPages = 1;

    PaginatedResult<String> result = PaginatedResult.of(items, totalItems, totalPages);

    assertNotNull(result);
    assertEquals(1, result.getItems().size());
    assertEquals(1, result.getTotalItems());
    assertEquals(1, result.getTotalPages());
  }
}
