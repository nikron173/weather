package com.nikron.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@ToString(exclude = {"locations"})
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true,
            nullable = false)
    private String login;

    @Column(unique = true,
            nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "users_locations",
            joinColumns = {
                    @JoinColumn (name = "user_id")
            },
            inverseJoinColumns = {
                    @JoinColumn (name = "location_id")
            }
    )
    private List<Location> locations = new ArrayList<>();

    public boolean addLocation(Location location) {
        return locations.add(location);
    }

    public boolean deleteLocation(Location location) {
        return locations.remove(location);
    }
}
