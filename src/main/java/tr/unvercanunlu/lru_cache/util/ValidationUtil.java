package tr.unvercanunlu.lru_cache.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtil {

  public static void checkCapacityValid(int capacity) throws IllegalArgumentException {
    if (capacity <= 0) {
      String message = "Capacity should be a positive number! capacity=%d".formatted(capacity);
      log.error(message);
      throw new IllegalArgumentException(message);
    }
  }

  public static <T> void checkValueExists(T value) throws IllegalArgumentException {
    if (value == null) {
      String message = "Value missing!";
      log.error(message);
      throw new IllegalArgumentException(message);
    }

  }

}
