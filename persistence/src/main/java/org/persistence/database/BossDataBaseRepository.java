package org.persistence.database;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.model.Boss;
import org.persistence.repository.BossRepository;

public class BossDataBaseRepository implements BossRepository {
    @Override
    public Boss save(Boss boss) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.save(boss);
                tx.commit();
                return boss;
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                    return null;
                }
            }
            return null;
        }
    }

    @Override
    public Boss delete(Long idBoss) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Boss criteria = (Boss) session.createQuery("from Boss where id = :idParam", Boss.class)
                        .setParameter("idParam", idBoss)
                        .setMaxResults(1)
                        .uniqueResult();
                session.delete(criteria);
                tx.commit();
                Boss p = new Boss();
                p.setId(idBoss);
                p.setFirstName("");
                p.setLastName("");
                p.setEmail("");
                p.setPassword("");
                return p;
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Boss update(Boss boss) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Boss bossToUpdate = (Boss) session.load(Boss.class, boss.getId());
                bossToUpdate.setFirstName(boss.getFirstName());
                bossToUpdate.setLastName(boss.getLastName());
                bossToUpdate.setEmail(boss.getEmail());
                bossToUpdate.setPassword(boss.getPassword());
                tx.commit();
                return boss;
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Boss findOne(Long idBoss) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Boss boss = session.get(Boss.class, idBoss);
                tx.commit();
                return boss;
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                return null;
            }
        }
    }

    @Override
    public Iterable<Boss> findAll() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Iterable<Boss> bosses = session.createQuery("from Boss", Boss.class).stream().toList();
                tx.commit();
                return bosses;
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                return null;
            }
        }
    }

    @Override
    public long checkBossAccountExistence(String email, String password) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Boss boss = session.createQuery("from Boss where email = :emailParameter and password = :passwordParameter", Boss.class)
                        .setParameter("emailParameter", email)
                        .setParameter("passwordParameter", password)
                        .setMaxResults(1)
                        .uniqueResult();
                tx.commit();
                System.out.println(boss);
                if(boss != null) {
                    return 1;
                }
            } catch (RuntimeException ex) {
                if (tx != null) {
                    tx.rollback();
                }
                return 0;
            }
        }
        return 0;
    }

}
