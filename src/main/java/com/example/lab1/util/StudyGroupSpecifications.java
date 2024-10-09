package com.example.lab1.util;

import com.example.lab1.entity.StudyGroup;
import org.springframework.data.jpa.domain.Specification;

public class StudyGroupSpecifications {

    public static Specification<StudyGroup> filterByStringField(String fieldName, String fieldValue) {
        return (root, query, criteriaBuilder) -> {
            // Проверяем наличие поля и фильтруем только по строковым полям
            if (root.get(fieldName).getJavaType() == String.class) {
                return criteriaBuilder.equal(root.get(fieldName), fieldValue);
            }
            throw new IllegalArgumentException("Field " + fieldName + " is not a string field");
        };
    }
}