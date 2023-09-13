package com.astontech.lms.rest;

import com.astontech.lms.domain.Path;
import com.astontech.lms.domain.PathDTO;
import com.astontech.lms.repositories.PathRepository;
import com.astontech.lms.services.PathService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
public class PathRestController {

    private static final Logger logger = LoggerFactory.getLogger(PathRestController.class);
    PathService pathService;
    PathRepository pathRepository;

    public PathRestController(PathService pathService, PathRepository pathRepository) {
        this.pathService = pathService;
        this.pathRepository = pathRepository;
    }

    @ApiOperation(value = "Get all paths from the database", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of paths"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "No paths were found in the database")
    })
    @GetMapping("/api/paths")
    public ResponseEntity getPaths(@RequestParam(required = false, name = "name") String name) {
        Path path;
        Map<String, Object> makeShiftPath;
        if (name != null) {
            path = pathService.findByName(name).orElse(null);
            makeShiftPath = pathService.createPath(path.getId());
            return ResponseEntity.status(HttpStatus.OK).body(makeShiftPath);
        }

        List<Object> foundPaths = pathService.findAll();
        if (!foundPaths.isEmpty()) {
            return ResponseEntity.ok(foundPaths);
        } else if (foundPaths.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @ApiOperation(value = "Get path from the database by id", response = Path.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved path by id"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "No path with the given id found in the database")
    })
    @GetMapping("/api/paths/{id}")
    public ResponseEntity<Map<String, Object>> getPathById(@PathVariable String id) {
        Map<String, Object> makeShiftPath = pathService.createPath(id);

        if (makeShiftPath.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(makeShiftPath);
        }

        return ResponseEntity.status(HttpStatus.OK).body(makeShiftPath);
    }

    @ApiOperation(value = "Save path to the database", response = Path.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully saved path"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 422, message = "The given path could not be processed")
    })
    @PostMapping(value = "/api/paths")
    public ResponseEntity<Path> savePath(@RequestBody PathDTO path) {
        Path savedPath;

        try {
            savedPath = pathService.savePathFromDTO(path);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null);
        }

        if (savedPath.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(savedPath);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedPath);
    }

    @ApiOperation(value = "Update path in the database", response = Path.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated path"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 422, message = "The given path could not be processed")
    })
    @PutMapping(value = "/api/paths")
    public ResponseEntity<Path> updatePath(@RequestBody PathDTO path) {
        Path savedPath;
        savedPath = pathService.updatePathFromDTO(path);

        if (savedPath.getId() == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(savedPath);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedPath);
    }

    @ApiOperation(value = "Delete path from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted path from the database"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "No path with the given id found in the database")
    })
    @DeleteMapping("/api/paths/{id}")
    public ResponseEntity deletePathById(@PathVariable String id) {
        pathService.deletePathById(id);

        Optional<Path> deletedPath = pathService.findById(id);

        if (!deletedPath.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        System.out.println("Could not find Path with id: " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping("/api/paths")
    public ResponseEntity deleteAll() {
        pathService.deleteAll();

        List<Path> foundPaths = pathRepository.findAll();

        if (foundPaths.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("All paths deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not successfully delete all paths.");
        }
    }

}
