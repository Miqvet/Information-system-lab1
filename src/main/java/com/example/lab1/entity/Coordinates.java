package com.example.lab1.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Entity
@Data
@Table(name = "coordinates")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "X coordinate is required")
    @Min(value = -407, message = "X должна быть >= -407")
    @Max(value = 500, message = "X меньше 500")
    private Long x; //Значение поля должно быть больше -407, Поле не может быть null

    @NotNull
    private Integer y; //Поле не может быть null
}
