package commons.repository;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public class InMemoryRepository<E, ID> implements Repository<E, ID> {
    protected final List<E> entities;
    protected final Function<E, ID> idGetter;

    public ID save(E entity) {
        entities.add(entity);
        return getId(entity);
    }

    @Override
    public boolean removeById(ID id) {
        return findById(id)
                .map(entities::remove)
                .getOrElse(false);
    }

    public Option<E> findById(ID id) {
        return entities
                .stream()
                .filter(e -> getId(e).equals(id))
                .findAny()
                .map(Option::of)
                .orElse(Option.none());
    }

    @Override
    public Collection<E> findAll() {
        return new LinkedList<>(entities);
    }

    @Override
    public boolean existsById(ID id) {
        return entities
                .stream()
                .anyMatch(entity -> getId(entity).equals(id));
    }

    private ID getId(E entity) {
        return idGetter.apply(entity);
    }
}