package units.userService.user.application.dto;

import com.app.userService.user.application.dto.PaginatedUsersDTO;
import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;
import org.junit.jupiter.api.Test;
import units.userService._mocks.UserMockUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaginatedUsersDTOTest {

  private static final String BASE_URL = "http://api.example.com/users";

  @Test
  void firstPage_shouldHaveNextUrl_andNoPreviousUrl() {
    PaginatedResult<User> result = PaginatedResult.of(List.of(UserMockUtil.createMockUser()), 30, 3);

    PaginatedUsersDTO dto = PaginatedUsersDTO.of(result, 0, 10, BASE_URL);

    assertNull(dto.previousPageUrl());
    assertNotNull(dto.nextPageUrl());
  }

  @Test
  void lastPage_shouldHavePreviousUrl_andNoNextUrl() {
    PaginatedResult<User> result = PaginatedResult.of(List.of(UserMockUtil.createMockUser()), 30, 3);

    PaginatedUsersDTO dto = PaginatedUsersDTO.of(result, 2, 10, BASE_URL);

    assertNotNull(dto.previousPageUrl());
    assertNull(dto.nextPageUrl());
  }

  @Test
  void middlePage_shouldHaveBothUrls() {
    PaginatedResult<User> result = PaginatedResult.of(List.of(UserMockUtil.createMockUser()), 30, 3);

    PaginatedUsersDTO dto = PaginatedUsersDTO.of(result, 1, 10, BASE_URL);

    assertNotNull(dto.previousPageUrl());
    assertNotNull(dto.nextPageUrl());
  }

  @Test
  void urls_shouldUseProvidedBaseUrl_notRequestHost() {
    PaginatedResult<User> result = PaginatedResult.of(List.of(UserMockUtil.createMockUser()), 30, 3);

    PaginatedUsersDTO dto = PaginatedUsersDTO.of(result, 1, 10, BASE_URL);

    assertTrue(dto.nextPageUrl().startsWith(BASE_URL));
    assertTrue(dto.previousPageUrl().startsWith(BASE_URL));
    assertFalse(dto.nextPageUrl().contains("attacker.com"));
    assertFalse(dto.previousPageUrl().contains("attacker.com"));
  }

  @Test
  void urls_shouldContainCorrectPageAndSizeParams() {
    PaginatedResult<User> result = PaginatedResult.of(List.of(UserMockUtil.createMockUser()), 30, 3);

    PaginatedUsersDTO dto = PaginatedUsersDTO.of(result, 1, 10, BASE_URL);

    assertEquals(BASE_URL + "?page=2&size=10", dto.nextPageUrl());
    assertEquals(BASE_URL + "?page=0&size=10", dto.previousPageUrl());
  }

  @Test
  void singlePage_shouldHaveNoNavigationUrls() {
    PaginatedResult<User> result = PaginatedResult.of(List.of(UserMockUtil.createMockUser()), 5, 1);

    PaginatedUsersDTO dto = PaginatedUsersDTO.of(result, 0, 10, BASE_URL);

    assertNull(dto.nextPageUrl());
    assertNull(dto.previousPageUrl());
  }
}