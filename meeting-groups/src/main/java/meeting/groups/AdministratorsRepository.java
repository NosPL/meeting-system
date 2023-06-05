package meeting.groups;

import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.List;
import java.util.function.Function;

interface AdministratorsRepository extends Repository<Administrator, String> {

    class InMemory extends InMemoryRepository<Administrator, String> implements AdministratorsRepository {

        public InMemory(List<Administrator> entities, Function<Administrator, String> idGetter) {
            super(entities, idGetter);
        }
    }
}