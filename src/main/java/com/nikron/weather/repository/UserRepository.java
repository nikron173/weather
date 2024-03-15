package com.nikron.weather.repository;

import com.nikron.weather.entity.Location;
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
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<User> findByEmail(String email) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<User> users = em.createQuery("FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultList();
            em.getTransaction().commit();
            if (users.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(users.get(0));
        } catch (Exception e) {
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<User> findByLogin(String login) {
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
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
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
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
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
                throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
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
                throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
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
                throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public List<Location> findUserLocationAll(Long id) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            User userDb = em.find(User.class, id);
            List<Location> locations = userDb.getLocations().stream().toList();
            em.flush();
            em.getTransaction().commit();
            return locations;
        } catch (Exception e) {
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Location> findUserLocation(Long userId, Long locationId) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<Location> locations = em
                    .createQuery(
                            "FROM Location l JOIN l.users u WHERE u.id = :userId AND l.id = :locationId",
                            Location.class)
                    .setParameter("userId", userId)
                    .setParameter("locationId", locationId)
                    .getResultList();
            em.getTransaction().commit();
            if (locations.isEmpty()) return Optional.empty();
            return Optional.of(locations.get(0));
        } catch (Exception e) {
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void deleteUserLocation(Long userId, Location location) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                User userDb = em.find(User.class, userId);
                userDb.deleteLocation(location);
                em.flush();
                transactional.commit();
            } catch (Exception e) {
                if (Objects.nonNull(transactional) && transactional.isActive()) {
                    transactional.rollback();
                }
                throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void addUserLocation(Long userId, Location location) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                User userDb = em.find(User.class, userId);
                if (!userDb.getLocations().contains(location)) {
                    userDb.addLocation(location);
                    em.flush();
                }
                transactional.commit();
            } catch (Exception e) {
                if (Objects.nonNull(transactional) && transactional.isActive()) {
                    transactional.rollback();
                }
                throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new DatabaseException(String.format("Database error - %s", e.getMessage()),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
