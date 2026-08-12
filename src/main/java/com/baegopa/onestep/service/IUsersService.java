package com.baegopa.onestep.service;

import com.baegopa.onestep.dto.UsersDTO;

public interface IUsersService {
    UsersDTO getUserIdExists(UsersDTO pDTO) throws Exception;

    UsersDTO getEmailExists(UsersDTO pDTO) throws Exception;

    int insertUsers(UsersDTO pDTO) throws Exception;
}
