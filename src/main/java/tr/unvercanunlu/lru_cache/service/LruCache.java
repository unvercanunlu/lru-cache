package tr.unvercanunlu.lru_cache.service;

import java.util.Optional;

public interface LruCache<K, V> extends Cache<K, V> {

  Optional<V> peekOldest();

  Optional<V> peekNewest();

  int getCapacity();

  int getRemainingCapacity();

  void resize(int capacity);

}
