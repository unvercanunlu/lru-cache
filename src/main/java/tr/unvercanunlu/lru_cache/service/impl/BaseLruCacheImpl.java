package tr.unvercanunlu.lru_cache.service.impl;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.lru_cache.exception.NoSuchElementException;
import tr.unvercanunlu.lru_cache.model.Node;
import tr.unvercanunlu.lru_cache.service.Cache;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseLruCacheImpl<K, V> implements Cache<K, V> {

  @Getter
  private final int capacity;

  private final Map<K, Node<K, V>> map = new HashMap<>();

  private Node<K, V> head;
  private Node<K, V> tail;

  @Override
  public V get(K key) {
    if (!map.containsKey(key)) {
      log.info("There is no such an element with key={} in the map.", key);
      throw new NoSuchElementException(key);
    }

    Node<K, V> temp = map.get(key);

    if (!((head == temp) && (tail == temp) && (map.size() == 1))) {
      if (temp.getPrevious() != null) {
        temp.getPrevious().setNext(temp.getNext());
      }

      if (temp.getNext() != null) {
        temp.getNext().setPrevious(temp.getPrevious());
      }

      temp.setNext(head);
      temp.setPrevious(null);

      if (head != null) {
        head.setPrevious(temp);
      }

      head = temp;
    }

    log.info("Retrieved element with key={} is moved to head.", key);

    return temp.getValue();
  }

  @Override
  public void set(K key, V value) {
    remove(key);

    Node<K, V> temp = new Node<>(key, value);

    if ((head == null) && (tail == null) && map.isEmpty()) {
      temp.setNext(null);
      temp.setPrevious(null);

      map.put(key, temp);

      head = temp;
      tail = temp;

      log.info("First element with key={} is added to map.", key);

    } else {
      temp.setNext(head);
      temp.setPrevious(null);

      if (head != null) {
        head.setPrevious(temp);
      }

      map.put(key, temp);

      head = temp;

      log.info("New added element with key={} is moved to head position.", key);
    }

    if (map.size() > capacity) {
      log.info("Cache capacity is exceed.");

      K evictedKey = tail.getKey();

      if ((head == tail) && (map.size() == 1)) {

        map.remove(head.getKey());

        head = null;
        tail = null;
      } else {
        Node<K, V> previous = tail.getPrevious();

        if (previous != null) {
          previous.setNext(null);
        }

        tail.setPrevious(null);

        map.remove(tail.getKey());

        tail = previous;
      }

      log.info("Element with key={} from tail is removed", evictedKey);
    }
  }

  @Override
  public void remove(K key) {
    if (!map.containsKey(key)) {
      log.info("Element with key={} doesn't exist in the cache. So, no need to do operation to remove it.", key);

      return;
    }

    Node<K, V> temp = map.get(key);

    if ((head == temp) && (tail == temp) && (map.size() == 1)) {
      head = null;
      tail = null;

      map.remove(temp.getKey());
    } else {
      if (temp.getPrevious() != null) {
        temp.getPrevious().setNext(temp.getNext());
      }

      if (temp.getNext() != null) {
        temp.getNext().setPrevious(temp.getPrevious());
      }

      temp.setPrevious(null);
      temp.setNext(null);

      map.remove(temp.getKey());
    }

    log.info("Element with key={} is removed from the cache.", key);
  }

  @Override
  public void clear() {
    head = null;
    tail = null;

    map.clear();

    log.info("Cache is cleared.");
  }

}
