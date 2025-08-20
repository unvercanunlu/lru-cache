# LRU Cache
A **thread-safe Least Recently Used (LRU) Cache** in Java using a `HashMap` + Doubly Linked List.  
Provides **O(1)** store/retrieve, automatic eviction, runtime resize, and validation utilities.

## Features
- O(1) store/retrieve with recency update
- Thread-safe with `ReadWriteLock`
- Auto-eviction when full
- Resize capacity at runtime
- Peek oldest/newest entries
- Clear & evict APIs
- Custom key validation (`isKeyValid`)
- Value validation (no null values)

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
cache.retrieve("a");         // promotes "a" to most recent
cache.store("c", "gamma");   // evicts "b"
System.out.println(cache.peekOldest()); // Optional[alpha]
````

## API
* `store(K,V)` — add/update element
* `retrieve(K)` — get & promote
* `evict(K)`, `clear()`, `checkExists(K)`
* `peekNewest()`, `peekOldest()`
* `resize(int)`, `getCapacity()`, `getRemainingCapacity()`
