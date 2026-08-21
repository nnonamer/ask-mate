package com.codecool.askmateoop.controller;

import com.codecool.askmateoop.controller.dto.LoginUserDTO;
import com.codecool.askmateoop.controller.dto.NewUserDTO;
import com.codecool.askmateoop.controller.dto.UserDTO;
import com.codecool.askmateoop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public int addNewUser(@RequestBody NewUserDTO user) {
        return userService.addNewUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginUserDTO user) {
        UserDTO loggedInUser = userService.login(user);

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(loggedInUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/points")
    public int getPointsById(@PathVariable int id){
        return userService.getPointsById(id);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable int id){
        return userService.getUserById(id);
    }
}
