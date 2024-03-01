package com.nikron.weather.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<K, V> {
    Optional<V> find(K id);

    List<V> findAll();

    V save(V object);

    V update(K id, V object);

    void delete(K id);
}
