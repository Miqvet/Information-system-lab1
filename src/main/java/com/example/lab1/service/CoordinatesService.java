package com.example.lab1.service;

import com.example.lab1.entity.Coordinates;
import com.example.lab1.repository.CoordinatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoordinatesService {

    @Autowired
    private CoordinatesRepository coordinatesRepository;

    public List<Coordinates> findAll() {
        return coordinatesRepository.findAll();
    }

    public Coordinates findById(Long id) {
        return coordinatesRepository.findById(id).orElse(null);
    }

    public void save(Coordinates coordinates) {
        coordinatesRepository.save(coordinates);
    }

    public void deleteById(Long id) {
        coordinatesRepository.deleteById(id);
    }
}