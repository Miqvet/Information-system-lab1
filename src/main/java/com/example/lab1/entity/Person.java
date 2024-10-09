package com.example.lab1.entity;

import com.example.lab1.entity.enums.Color;
import com.example.lab1.entity.enums.Country;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    @Size(min = 1, max = 42, message = "Размер строки должен быть от 1 до 42")
    private String passportID; //Строка не может быть пустой, Длина строки не должна быть больше 42, Значение этого поля должно быть уникальным, Поле может быть null

    @NotNull
    @NotEmpty(message = "Строка не должна быть пустой")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull
    @Enumerated(EnumType.STRING)
    private Color eyeColor; //Поле может быть null

    @NotNull
    @Enumerated(EnumType.STRING)
    private Color hairColor; //Поле не может быть null

    @Valid
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Location location; //Поле не может быть null

    @NotNull
    @Enumerated(EnumType.STRING)
    private Country nationality; //Поле не может быть null


}
