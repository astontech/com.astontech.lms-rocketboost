package com.astontech.lms.repositories;

import com.astontech.lms.domain.Path;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PathRepository extends MongoRepository<Path, String> {

    Optional<Path> findByTitle(String title);

}
