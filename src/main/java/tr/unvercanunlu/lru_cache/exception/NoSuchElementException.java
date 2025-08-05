package tr.unvercanunlu.lru_cache.exception;

import lombok.Getter;

public class NoSuchElementException extends RuntimeException {

  @Getter
  private final Object key;

  public NoSuchElementException(Object key) {
    super(String.format("No such an element with key=%s", key));

    this.key = key;
  }

}
