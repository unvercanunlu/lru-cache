package tr.unvercanunlu.lru_cache.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class Node<K, V> {

  private final K key;
  private final V value;

  @Setter
  private Node<K, V> previous = null;

  @Setter
  private Node<K, V> next = null;

}
