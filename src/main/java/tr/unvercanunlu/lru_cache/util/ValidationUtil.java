package tr.unvercanunlu.lru_cache.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationUtil {

  public static void checkCapacityValid(int capacity) throws IllegalArgumentException {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity should be a positive number! capacity=%d".formatted(capacity));
    }
  }

}
