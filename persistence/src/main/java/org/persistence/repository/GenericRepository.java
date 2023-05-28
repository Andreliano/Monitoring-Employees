package org.persistence.repository;

import org.model.Entity;

public interface GenericRepository<ID, E extends Entity<ID>> {
    E save(E entity);

    E delete(ID idEntity);

    E update(E entity);

    E findOne(ID idEntity);

    Iterable<E> findAll();
}
