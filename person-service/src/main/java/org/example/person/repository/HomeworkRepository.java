package org.example.person.repository;

import org.example.person.dto.homework.HomeworkDto;

import java.util.List;
import java.util.Map;

public interface HomeworkRepository {
    Map<Integer, HomeworkDto> findHomeworksByHomeworkIdIn(List<Integer> homeworkIds);

    HomeworkDto getReferenceById(Integer homeworkId);
}
