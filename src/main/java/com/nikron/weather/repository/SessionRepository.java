package com.nikron.weather.repository;

import com.nikron.weather.entity.Session;
import com.nikron.weather.exception.DatabaseException;
import com.nikron.weather.util.BuildEntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SessionRepository implements Repository<String, Session> {

    private final static SessionRepository INStANCE = new SessionRepository();

    public static SessionRepository getInstance() {
        return INStANCE;
    }

    @Override
    public Optional<Session> find(String key) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            Optional<Session> session = Optional.ofNullable(em.find(Session.class, key));
            em.getTransaction().commit();
            return session;
        } catch (Exception e) {
            System.out.println(e);
            throw new DatabaseException("Ошибка получения объекта из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Session> findByUserId(Long id) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<Session> sessions = em.createQuery("FROM Session s WHERE s.user.id = :id", Session.class)
                    .setParameter("id", id)
                    .getResultList();
            em.getTransaction().commit();
            if (sessions.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(sessions.get(0));
        } catch (Exception e) {
            throw new DatabaseException("Ошибка получения объекта из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Session> findAll() {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<Session> sessions = em.createQuery("FROM Session", Session.class)
                    .getResultList();
            em.getTransaction().commit();
            return sessions;
        } catch (Exception e) {
            throw new DatabaseException("Ошибка получения объектов из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Session save(Session value) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                em.persist(value);
                em.flush();
                transactional.commit();
                return value;
            } catch (Exception e) {
                if (Objects.nonNull(transactional) && transactional.isActive()) {
                    transactional.rollback();
                }
                throw new DatabaseException("Ошибка сохранения объекта в базу данных",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException("Ошибка сохранения объекта в базу данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Session update(String key, Session value) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                Session sessionDb = em.find(Session.class, key);
                sessionDb.setExpiresAt(value.getExpiresAt());
                em.flush();
                transactional.commit();
                return sessionDb;
            } catch (Exception e) {
                if (Objects.nonNull(transactional) && transactional.isActive()) {
                    transactional.rollback();
                }
                throw new DatabaseException("Ошибка обновления объекта в базе данных",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException("Ошибка обновления объекта в базе данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void delete(String id) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                em.createQuery("DELETE FROM Session s WHERE s.id = :id")
                        .setParameter("id", id)
                        .executeUpdate();
                transactional.commit();
            } catch (Exception e) {
                if (Objects.nonNull(transactional) && transactional.isActive()) {
                    transactional.rollback();
                }
                throw new DatabaseException("Ошибка удаления объекта в базе данных",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException("Ошибка удаления объекта в базе данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
