package commons.repository;

import io.vavr.control.Option;

import java.util.Collection;

public interface Repository<E, ID> {
    ID save(E e);

    boolean removeById(ID id);

    Option<E> findById(ID id);

    Collection<E> findAll();

    boolean existsById(ID id);
}