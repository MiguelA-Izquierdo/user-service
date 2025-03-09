package units.userService.user.domain.model;

import com.app.userService.user.domain.model.PaginatedResult;
import org.junit.jupiter.api.Assertions;
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

    Assertions.assertNotNull(result);
    Assertions.assertEquals(items, result.getItems());
    Assertions.assertEquals(totalItems, result.getTotalItems());
    Assertions.assertEquals(totalPages, result.getTotalPages());
  }

  @Test
  void testEmptyResult() {
    List<String> items = List.of();
    long totalItems = 0;
    int totalPages = 0;

    PaginatedResult<String> result = PaginatedResult.of(items, totalItems, totalPages);

    Assertions.assertNotNull(result);
    Assertions.assertTrue(result.getItems().isEmpty());
    Assertions.assertEquals(0, result.getTotalItems());
    Assertions.assertEquals(0, result.getTotalPages());
  }

  @Test
  void testPaginationWithOneItem() {
    List<String> items = List.of("singleItem");
    long totalItems = 1;
    int totalPages = 1;

    PaginatedResult<String> result = PaginatedResult.of(items, totalItems, totalPages);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(1, result.getItems().size());
    Assertions.assertEquals(1, result.getTotalItems());
    Assertions.assertEquals(1, result.getTotalPages());
  }
}
