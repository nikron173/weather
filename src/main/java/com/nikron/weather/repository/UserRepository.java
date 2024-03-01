package com.nikron.weather.repository;

import com.nikron.weather.entity.User;
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
public class UserRepository implements Repository<Long, User> {

    private static final UserRepository INSTANCE = new UserRepository();

    public static UserRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<User> find(Long key) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            Optional<User> user = Optional.ofNullable(em.find(User.class, key));
            em.getTransaction().commit();
            return user;
        } catch (Exception e) {
            throw new DatabaseException("Ошибка получения объекта из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<User> find(String login) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<User> users = em.createQuery("FROM User u WHERE u.login = :login", User.class)
                                .setParameter("login", login)
                                .getResultList();
            em.getTransaction().commit();
            if (users.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(users.get(0));
        } catch (Exception e) {
            System.out.println(e);
            throw new DatabaseException("Ошибка получения объекта из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<User> findAll() {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<User> users = em.createQuery("FROM User", User.class).getResultList();
            em.getTransaction().commit();
            return users;
        } catch (Exception e) {
            throw new DatabaseException("Ошибка получения объектов из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public User save(User value) {
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
    public User update(Long key, User value) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                User userDb = em.find(User.class, key);
                userDb.setPassword(value.getPassword());
                em.flush();
                transactional.commit();
                return userDb;
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
    public void delete(Long id) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                em.createQuery("DELETE FROM User u WHERE u.id = :id")
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
