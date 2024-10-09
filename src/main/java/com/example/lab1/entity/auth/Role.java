package com.example.lab1.entity.auth;

import com.example.lab1.entity.enums.RoleName;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;
}


