package com.baegopa.onestep.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UsersDTO {

    private String userid;

    private String userName;

    private String userRole;

    private String loginId;

    private String password;

    private String phone;

    private String email;

    private String pushToken;

    private String regDt;

    private String updDt;

    private String existsYn;

    private int authNumber;

}
