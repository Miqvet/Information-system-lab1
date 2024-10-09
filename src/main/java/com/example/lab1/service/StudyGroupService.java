package com.example.lab1.service;
import com.example.lab1.entity.Person;
import com.example.lab1.entity.StudyGroup;
import com.example.lab1.repository.StudyGroupRepository;
import com.example.lab1.util.StudyGroupSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudyGroupService {

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    public Page<StudyGroup> getAll(int page, int size, String sortBy, String filter) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(sortBy));
        if (filter != null && !filter.isEmpty()) {
            return studyGroupRepository.findByNameContaining(filter, pageable);
        }
        return studyGroupRepository.findAll(pageable);
    }

    public StudyGroup getById(int id) {
        return studyGroupRepository.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public void save(StudyGroup studyGroup) {
        studyGroupRepository.save(studyGroup);
    }

    public void deleteById(int id) {
        studyGroupRepository.deleteById(id);
    }

    // Метод для обновления объекта
    public void update(int id, StudyGroup updatedStudyGroup) {
        StudyGroup existingStudyGroup = studyGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StudyGroup with id " + id + " not found"));
        // Обновляем поля
        existingStudyGroup.setName(updatedStudyGroup.getName());
        existingStudyGroup.setCoordinates(updatedStudyGroup.getCoordinates());
        existingStudyGroup.setStudentsCount(updatedStudyGroup.getStudentsCount());
        existingStudyGroup.setExpelledStudents(updatedStudyGroup.getExpelledStudents());
        existingStudyGroup.setTransferredStudents(updatedStudyGroup.getTransferredStudents());
        existingStudyGroup.setFormOfEducation(updatedStudyGroup.getFormOfEducation());
        existingStudyGroup.setShouldBeExpelled(updatedStudyGroup.getShouldBeExpelled());
        existingStudyGroup.setAverageMark(updatedStudyGroup.getAverageMark());
        existingStudyGroup.setSemesterEnum(updatedStudyGroup.getSemesterEnum());
        existingStudyGroup.setGroupAdmin(updatedStudyGroup.getGroupAdmin());
        studyGroupRepository.save(existingStudyGroup); // Сохраняем обновленный объект
    }
    public Page<StudyGroup> findFilteredAndSorted(int page, int pageSize, String filterField, String filterValue, String sortBy) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(sortBy));

        Specification<StudyGroup> spec = Specification.where(null);

        if (!filterField.isEmpty() && !filterValue.isEmpty()) {
            spec = spec.and(StudyGroupSpecifications.filterByStringField(filterField, filterValue));
        }

        return studyGroupRepository.findAll(spec, pageable);
    }

    public long countByShouldBeExpelledLessThan(int threshold) {
        return studyGroupRepository.countByShouldBeExpelledLessThan(threshold);
    }

    public long countByShouldBeExpelledGreaterThan(int threshold) {
        return studyGroupRepository.countByShouldBeExpelledGreaterThan(threshold);
    }

    public List<Person> findUniqueGroupAdmins() {
        return studyGroupRepository.findAll()
                .stream()
                .map(StudyGroup::getGroupAdmin)
                .distinct()
                .collect(Collectors.toList());
    }

    public void expelGroupStudents(int groupId) throws NoSuchElementException {
        StudyGroup group = studyGroupRepository.findById(groupId).orElseThrow();
        group.setExpelledStudents((int) (group.getStudentsCount() + group.getExpelledStudents()));
        group.setStudentsCount(0);
        group.setShouldBeExpelled(0);
        studyGroupRepository.save(group);
    }

    public void transferStudents(int fromGroupId, int toGroupId) {
        StudyGroup fromGroup = studyGroupRepository.findById(fromGroupId).orElseThrow();
        StudyGroup toGroup = studyGroupRepository.findById(toGroupId).orElseThrow();

        int studentsToTransfer =  fromGroup.getStudentsCount();

        fromGroup.setStudentsCount(0);
        fromGroup.setExpelledStudents(fromGroup.getExpelledStudents() + studentsToTransfer);
        fromGroup.setExpelledStudents(fromGroup.getExpelledStudents() + studentsToTransfer);
        fromGroup.setShouldBeExpelled(0);

        toGroup.setStudentsCount(toGroup.getStudentsCount() + studentsToTransfer);
        toGroup.setStudentsCount((toGroup.getTransferredStudents() + studentsToTransfer));
        studyGroupRepository.save(fromGroup);
        studyGroupRepository.save(toGroup);
    }



}