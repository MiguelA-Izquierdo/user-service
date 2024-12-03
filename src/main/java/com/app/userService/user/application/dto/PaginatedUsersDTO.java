package com.app.userService.user.application.dto;

import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.infrastructure.api.dto.UserResponseDTO;

import java.util.List;

public record PaginatedUsersDTO(List<UserResponseDTO> users, int totalPages, long totalElements, int currentPage,
                                String nextPageUrl, String previousPageUrl) {

  public static PaginatedUsersDTO of(PaginatedResult<User> paginatedResult, int currentPage, long size, String baseUrl) {
    List<UserResponseDTO> users = paginatedResult.getItems().stream().map(UserResponseDTO::Of).toList();

    boolean hasNextPage = currentPage < paginatedResult.getTotalPages() - 1;
    boolean hasPreviousPage = currentPage > 0;

    String nextPageUrl = hasNextPage ? String.format("%s?page=%d&size=%d", baseUrl, currentPage + 1, size) : null;
    String previousPageUrl = hasPreviousPage ? String.format("%s?page=%d&size=%d", baseUrl, currentPage - 1, size) : null;


    return new PaginatedUsersDTO(
      users,
      paginatedResult.getTotalPages(),
      paginatedResult.getTotalItems(),
      currentPage,
      nextPageUrl,
      previousPageUrl);
  }
}
