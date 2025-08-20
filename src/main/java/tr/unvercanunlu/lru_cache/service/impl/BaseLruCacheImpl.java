package tr.unvercanunlu.lru_cache.service.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.lru_cache.model.DoublyLinkedNode;
import tr.unvercanunlu.lru_cache.service.LruCache;
import tr.unvercanunlu.lru_cache.util.ValidationUtil;

@Slf4j
public abstract class BaseLruCacheImpl<K, V> implements LruCache<K, V> {

  @Getter
  private int capacity;

  private final ConcurrentMap<K, DoublyLinkedNode<K, V>> pairs = new ConcurrentHashMap<>();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  // shortcuts
  private DoublyLinkedNode<K, V> head;
  private DoublyLinkedNode<K, V> tail;

  protected BaseLruCacheImpl(int capacity) {
    ValidationUtil.checkCapacityValid(capacity);
    this.capacity = capacity;
  }

  protected abstract boolean isKeyValid(K key);

  @Override
  public Optional<V> retrieve(K key) {
    validateKey(key);

    lock.writeLock().lock();
    try {
      Optional<DoublyLinkedNode<K, V>> retrieved = Optional.ofNullable(
          pairs.get(key)
      );

      retrieved.ifPresentOrElse(node -> {
        log.info("Element with key={} is retrieved from the cache.", key);
        removeNode(node);
        addNodeToHead(node);
      }, () -> log.warn("There is no such an element with key={} in the cache.", key));

      return retrieved.map(DoublyLinkedNode::getValue);
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void store(K key, V value) {
    validateKey(key);

    lock.writeLock().lock();
    try {
      Optional<DoublyLinkedNode<K, V>> stored = Optional.ofNullable(
          pairs.get(key)
      );

      stored.ifPresent(node -> {
        node.setValue(value);
        log.info("Value of element with key={} is updated in the cache.", key);
      });

      DoublyLinkedNode<K, V> node = stored.orElseGet(() -> {
        DoublyLinkedNode<K, V> created = new DoublyLinkedNode<>(key, value, null, null);
        pairs.put(key, created);
        log.info("Element with key={} is stored in the cache.", key);
        return created;
      });

      removeNode(node);
      addNodeToHead(node);

      if (pairs.size() > capacity) {
        log.warn("Cache capacity exceeded! size={} capacity={}", pairs.size(), capacity);
        shrinkFromTail();
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void evict(K key) {
    validateKey(key);

    lock.writeLock().lock();
    try {
      Optional.ofNullable(
          pairs.remove(key)
      ).ifPresentOrElse(node -> {
        log.info("Element with key={} is evicted from the cache.", key);
        removeNode(node);
      }, () -> log.warn("There is no such an element with key={} in the cache.", key));
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void clear() {
    lock.writeLock().lock();
    try {
      pairs.clear();
      log.info("All elements are removed from the cache.");
      clearShortcuts();
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public boolean checkExists(K key) {
    validateKey(key);

    lock.readLock().lock();
    try {
      boolean result = pairs.containsKey(key);
      if (result) {
        log.info("Element with key={} exists in the cache.", key);
      }
      return result;
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public int size() {
    lock.readLock().lock();
    try {
      int size = pairs.size();
      log.info("The cache contains {} elements", size);
      return size;
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public Optional<V> peekNewest() {
    lock.readLock().lock();
    try {
      return Optional.ofNullable(head)
          .map(DoublyLinkedNode::getValue);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public Optional<V> peekOldest() {
    lock.readLock().lock();
    try {
      return Optional.ofNullable(tail)
          .map(DoublyLinkedNode::getValue);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public int getRemainingCapacity() {
    lock.readLock().lock();
    try {
      return (capacity - pairs.size());
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void resize(int capacity) {
    ValidationUtil.checkCapacityValid(capacity);
    lock.writeLock().lock();
    try {
      this.capacity = capacity;
      while (pairs.size() > capacity) {
        shrinkFromTail();
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  private void addNodeToHead(DoublyLinkedNode<K, V> node) {
    if (head == null) {
      head = node;
      tail = node;

      node.setPrevious(null);
      node.setNext(null);

      return;
    }

    node.setNext(head);
    node.setPrevious(null);

    head.setPrevious(node);

    head = node;

    log.debug("Element with key={} is moved to head position of the cache.", node.getKey());
  }

  private void removeNode(DoublyLinkedNode<K, V> node) {
    if (node == null) {
      return;
    }

    if (node == head) {
      head = node.getNext();
    }

    if (node == tail) {
      tail = node.getPrevious();
    }

    if (node.getPrevious() != null) {
      node.getPrevious().setNext(node.getNext());
    }

    if (node.getNext() != null) {
      node.getNext().setPrevious(node.getPrevious());
    }

    node.setPrevious(null);
    node.setNext(null);
  }

  private void clearShortcuts() {
    head = null;
    tail = null;
  }

  private void validateKey(K key) {
    if (!isKeyValid(key)) {
      String message = "Key invalid! key=%s".formatted(key);
      log.error(message);
      throw new IllegalArgumentException(message);
    }
  }

  private void shrinkFromTail() {
    if (tail != null) {
      DoublyLinkedNode<K, V> evicted = tail;
      removeNode(evicted);
      pairs.remove(evicted.getKey());
      log.info("Element with key={} evicted due to shrink.", evicted.getKey());
    }
  }

}
