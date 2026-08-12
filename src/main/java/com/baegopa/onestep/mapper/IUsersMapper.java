package com.baegopa.onestep.mapper;

import com.baegopa.onestep.dto.UsersDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUsersMapper {
    int insertUsers(UsersDTO pDTO) throws Exception;

    UsersDTO getUserIdExists(UsersDTO pDTO) throws Exception;

    UsersDTO getEmailExists(UsersDTO pDTO) throws Exception;


}
