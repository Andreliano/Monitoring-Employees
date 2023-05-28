package org.persistence.repository;

import org.model.Boss;

public interface BossRepository extends GenericRepository<Long, Boss> {
    long checkBossAccountExistence(String email, String password);
}
