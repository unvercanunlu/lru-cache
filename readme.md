# LRU Cache
A **thread-safe Least Recently Used (LRU) Cache** in Java using `ConcurrentHashMap` + Doubly Linked List.  
Provides O(1) get/put, automatic eviction, and capacity management.

## Features
- O(1) store/retrieve with recency update
- Thread-safe (`ReadWriteLock`)
- Automatic eviction when full
- Resize capacity at runtime
- Peek oldest/newest entries
- Clear & evict APIs
- Custom key validation via `isKeyValid`

## Usage

```java
public class StringLruCache extends BaseLruCacheImpl<String, String> {
    public StringLruCache(int capacity) { super(capacity); }
    @Override protected boolean isKeyValid(String key) {
      return key != null && !key.isBlank();
    }
}

LruCache<String, String> cache = new StringLruCache(2);
cache.store("a", "alpha");
cache.store("b", "beta");
cache.retrieve("a");   // moves "a" to most recent
cache.store("c", "gamma"); // evicts "b"
System.out.println(cache.peekOldest()); // Optional[alpha]
````

## API
* `store(K,V)`, `retrieve(K)`
* `evict(K)`, `clear()`, `checkExists(K)`
* `peekNewest()`, `peekOldest()`
* `resize(int)`, `getCapacity()`, `getRemainingCapacity()`
