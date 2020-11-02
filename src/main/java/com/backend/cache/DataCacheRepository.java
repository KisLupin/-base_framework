package com.backend.cache;

import java.util.Optional;
import java.util.function.Supplier;

public interface DataCacheRepository<T> {
    Boolean add(T object);

    Boolean delete(String key);

    Optional<T> find(String key, Class<T> tClass);

    Optional<T> getOrElseUpdate(String key, Class<T> tClass, Supplier<? extends T> supplier);

    Boolean isAvailable();
}
