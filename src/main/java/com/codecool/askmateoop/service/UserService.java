package com.codecool.askmateoop.service;

import com.codecool.askmateoop.controller.dto.LoginUserDTO;
import com.codecool.askmateoop.controller.dto.NewUserDTO;
import com.codecool.askmateoop.controller.dto.UserDTO;
import com.codecool.askmateoop.dao.UsersDAO;
import com.codecool.askmateoop.dao.model.User;
import com.codecool.askmateoop.util.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UsersDAO usersDAO;

    @Autowired
    public UserService(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    public int addNewUser(NewUserDTO user) {
        NewUserDTO hashedUser = new NewUserDTO(user.username(), PasswordHasher.hash(user.password()));
        return usersDAO.addNewUser(hashedUser);
    }

    public UserDTO login(LoginUserDTO credentials) {
        User user = usersDAO.getUserByUsername(credentials.username());

        if (user == null || !user.password().equals(PasswordHasher.hash(credentials.password()))) {
            return null;
        }

        return new UserDTO(user.id(), user.username());
    }

    public int getPointsById(int id){
        return usersDAO.getUserByPoints(id);
    }

    public UserDTO getUserById(int id){
        User user = usersDAO.getUserById(id);
        return new UserDTO(user.id(), user.username());
    }
}
