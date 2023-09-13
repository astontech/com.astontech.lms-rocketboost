package com.astontech.lms.services;

import com.astontech.lms.domain.Path;
import com.astontech.lms.domain.PathDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PathService {
    List<Object> findAll();

    Optional<Path> findById(String id);

    Optional<Path> findByName(String name);

    Path savePathFromDTO(PathDTO path) throws Exception;

    Path updatePathFromDTO(PathDTO path);

    void deletePathById(String id);

    void deletePath(Path path);

    void deleteAll();

    Map<String, Object> createPath(String id);

}
