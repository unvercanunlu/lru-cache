package tr.unvercanunlu.lru_cache.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
public class DoublyLinkedNode<K, V> {

  @Getter
  private final K key;

  @Getter
  @Setter
  private V value;

  @Getter
  @Setter
  private DoublyLinkedNode<K, V> previous = null;

  @Getter
  @Setter
  private DoublyLinkedNode<K, V> next = null;

  // key based equals method
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof DoublyLinkedNode<?, ?> that)) {
      return false;
    }

    return Objects.equals(key, that.key);
  }

  // key based hashCode method
  @Override
  public int hashCode() {
    return Objects.hashCode(key);
  }

}
