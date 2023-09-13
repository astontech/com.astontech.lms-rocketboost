package com.astontech.lms.rest;

import com.astontech.lms.domain.UserProfile;
import com.astontech.lms.repositories.UserProfileRepository;
import com.astontech.lms.services.UserProfileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    UserProfileService userProfileService;
    UserProfileRepository userProfileRepository;

    public UserRestController(UserProfileService userProfileService, UserProfileRepository userProfileRepository) {
        this.userProfileService = userProfileService;
        this.userProfileRepository = userProfileRepository;
    }

    @ApiOperation(value = "Get all users from the database", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of users"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "No users were found in the database")
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        List<UserProfile> foundProfiles = userProfileService.findAll();

        if (!foundProfiles.isEmpty()) {
            return ResponseEntity.ok(foundProfiles);
        }
        else if (foundProfiles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(foundProfiles);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(foundProfiles);
        }
    }

    @ApiOperation(value = "Get a user from the database by their username", response = UserProfile.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "No employee exists with the given username")
    })
    @GetMapping("/users/{username}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable String username) {
        UserProfile userProfile = userProfileService.getProfile(username);

        if (userProfile.getId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userProfile);
    }

    @ApiOperation(value = "Get the user from the current session", response = UserProfile.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user"),
            @ApiResponse(code = 420, message = "You are not authenticated with LDAP; there is no session user")
    })
    @GetMapping("/users/session")
    public ResponseEntity<UserProfile> getSessionUser(HttpSession session) {
        if (session.getAttribute("username") != null) {
            String username = session.getAttribute("username").toString();
            return getProfile(username);
        }
        else {
            logger.error("Error: No session!");
            return ResponseEntity.status(420).body(null);
        }
    }

    @ApiOperation(value = "Saves a user to the database", response = UserProfile.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully saved user"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 422, message = "The user profile that was provided is missing required properties needed to populate the object")
    })
    @PostMapping("/users")
    public ResponseEntity<UserProfile> saveUser(@RequestBody UserProfile userProfile) {
        if (userProfile.getUsername().equals("")) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(userProfile);
        }

        else {
            UserProfile savedProfile = userProfileService.save(userProfile);
            return ResponseEntity.status(HttpStatus.OK).body(savedProfile);
        }
    }

    @PutMapping("/users")
    public ResponseEntity<UserProfile> updateUserPath(@RequestBody Map<String, String> paths) {
        UserProfile userProfile = userProfileService.updateProfilePaths(paths.get("path"), paths.get("username"));
        return ResponseEntity.ok(userProfile);
    }

    @ApiOperation(value = "Deletes all users in the database", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Users deleted successfully"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "An error occurred, all users were not successfully deleted.")
    })
    @DeleteMapping("/users")
    public ResponseEntity deleteAll() {
        userProfileService.deleteAll();

        List<UserProfile> foundUsers = userProfileService.findAll();

        if (foundUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("All users deleted successfully.");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not successfully delete all users.");
        }
    }

    @ApiOperation(value = "Deletes a user in the database", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted user"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "An error occurred, could not delete the specified user.")
    })
    @DeleteMapping("/users/{username}")
    public ResponseEntity deleteUser(@PathVariable String username) {
        userProfileService.deleteByUsername(username);

        UserProfile foundUser = userProfileService.findByUsername(username);

        if (!foundUser.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully.");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not successfully delete user.");
        }
    }

}