package tr.unvercanunlu.lru_cache.service;

import java.util.Optional;

public interface Cache<K, V> {

  Optional<V> retrieve(K key);

  void store(K key, V value);

  void evict(K key);

  void clear();

  boolean checkExists(K key);

  int size();

}
