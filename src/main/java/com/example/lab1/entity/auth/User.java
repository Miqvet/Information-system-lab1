package com.example.lab1.entity.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "app_user")  // Переименовываем таблицу
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, max = 50, message = "Не меньше 5 и не больше 50 символов")
    private String username;

    @Size(min = 5, message = "Не меньше 5 символов")
    private String password;

    private boolean wishToBeAdmin = false;

    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getName().name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Вы можете настроить это значение по своему усмотрению
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Вы можете настроить это значение по своему усмотрению
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Вы можете настроить это значение по своему усмотрению
    }

    @Override
    public boolean isEnabled() {
        return true; // Вы можете настроить это значение по своему усмотрению
    }
}
