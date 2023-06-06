package meeting.groups;

import commons.dto.AdministratorId;
import commons.repository.InMemoryRepository;
import commons.repository.Repository;

import java.util.List;
import java.util.function.Function;

interface AdministratorRepository extends Repository<Administrator, AdministratorId> {

    class InMemory extends InMemoryRepository<Administrator, AdministratorId> implements AdministratorRepository {

        InMemory(List<Administrator> entities, Function<Administrator, AdministratorId> idGetter) {
            super(entities, idGetter);
        }
    }
}