package com.codecool.askmateoop.dao;

import com.codecool.askmateoop.controller.dto.NewUserDTO;
import com.codecool.askmateoop.dao.model.User;

public interface UsersDAO {
    int addNewUser(NewUserDTO user);

    User getUserByUsername(String username);
}
