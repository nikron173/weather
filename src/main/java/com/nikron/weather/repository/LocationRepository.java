package com.nikron.weather.repository;

import com.nikron.weather.entity.Location;
import com.nikron.weather.exception.DatabaseException;
import com.nikron.weather.util.BuildEntityManagerUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class LocationRepository implements Repository<Long, Location> {

    private static final LocationRepository INSTANCE = new LocationRepository();

    public static LocationRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Optional<Location> find(Long id) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            Optional<Location> location = Optional.ofNullable(em.find(Location.class, id));
            em.getTransaction().commit();
            return location;
        } catch (Exception e) {
            throw new DatabaseException("Ошибка получения объекта из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Location> find(String name, BigDecimal latitude, BigDecimal longitude) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<Location> locations =
                    em.createQuery("FROM Location l " +
                                            "WHERE l.name = :name AND l.latitude = :latitude AND l.longitude = :longitude",
                                    Location.class)
                            .setParameter("name", name)
                            .setParameter("latitude", latitude)
                            .setParameter("longitude", longitude)
                            .getResultList();
            em.getTransaction().commit();
            if (locations.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(locations.get(0));
        } catch (Exception e) {
            System.out.println(e);
            throw new DatabaseException("Ошибка получения объекта из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<Location> find(BigDecimal latitude, BigDecimal longitude) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<Location> locations =
                    em.createQuery("FROM Location l " +
                                            "WHERE l.latitude = :latitude AND l.longitude = :longitude",
                                    Location.class)
                            .setParameter("latitude", latitude)
                            .setParameter("longitude", longitude)
                            .getResultList();
            em.getTransaction().commit();
            if (locations.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(locations.get(0));
        } catch (Exception e) {
            System.out.println(e);
            throw new DatabaseException("Ошибка получения объекта из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Location> findAll() {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            em.getTransaction().begin();
            List<Location> locations = em.createQuery("FROM Location", Location.class).getResultList();
            em.getTransaction().commit();
            return locations;
        } catch (Exception e) {
            throw new DatabaseException("Ошибка получения объектов из базы данных",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Location save(Location object) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                em.persist(object);
                em.flush();
                transactional.commit();
                return object;
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
    public Location update(Long id, Location object) {
        return null;
    }

    @Override
    public void delete(Long id) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                em.createQuery("DELETE FROM Location l WHERE l.id = :id")
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

    public void delete(String name, BigDecimal latitude, BigDecimal longitude) {
        try (EntityManager em = BuildEntityManagerUtil.getEntityManager()) {
            EntityTransaction transactional = em.getTransaction();
            try {
                transactional.begin();
                em.createQuery("DELETE FROM Location l " +
                                "WHERE l.name = :name AND l.latitude = :latitude AND l.longitude = :longitude")
                        .setParameter("name", name)
                        .setParameter("latitude", latitude)
                        .setParameter("longitude", longitude)
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
