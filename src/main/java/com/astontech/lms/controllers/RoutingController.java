package com.astontech.lms.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@CrossOrigin(origins = "*")
@Controller
public class RoutingController {

    @ApiOperation(value = "Route to index.html", response = String.class)
    @ApiResponses(value = {})
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @ApiOperation(value = "Route to login.html", response = String.class)
    @ApiResponses(value = {})
    @GetMapping("/login")
    public String giveLogin() {
        return "login";
    }

}
