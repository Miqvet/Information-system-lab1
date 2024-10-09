package com.example.lab1.entity;

import com.example.lab1.entity.auth.User;
import com.example.lab1.entity.enums.FormOfEducation;
import com.example.lab1.entity.enums.Semester;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "studyGroup")
public class StudyGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotNull
    @NotEmpty
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Valid
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Coordinates coordinates; //Поле не может быть null

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Min(1)
    @NotNull
    private Integer studentsCount; //Значение поля должно быть больше 0

    @Min(1)
    @NotNull
    private Integer expelledStudents; //Значение поля должно быть больше 0

    @Min(1)
    @NotNull
    private Integer transferredStudents;//Значение поля должно быть больше 0, Поле может быть null

    @NotNull
    @Enumerated(EnumType.STRING)
    private FormOfEducation formOfEducation; //Поле не может быть null

    @NotNull
    @Min(1)
    private Integer shouldBeExpelled; //Значение поля должно быть больше 0, Поле не может быть null

    @Min(1)
    @NotNull
    private double averageMark;//Значение поля должно быть больше 0

    @Enumerated(EnumType.STRING)
    private Semester semesterEnum; //Поле может быть null

    @Valid
    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    private Person groupAdmin; //Поле может быть null

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }
    @Valid
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy; // Владелец (создатель)
}