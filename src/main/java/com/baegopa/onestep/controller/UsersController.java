package com.baegopa.onestep.controller;

import com.baegopa.onestep.dto.MsgDTO;
import com.baegopa.onestep.dto.UsersDTO;
import com.baegopa.onestep.service.IUsersService;
import com.baegopa.onestep.util.CmmUtil;
import com.baegopa.onestep.util.EncryptUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Controller
public class UsersController {
    private final IUsersService usersService;

    @GetMapping(value = "usersRegForm")
    public String usersRegForm() {
        log.info("{}.users/usersRegForm",this.getClass().getName());

        return  "users/usersRegForm";
    }
    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UsersDTO getUserExists(HttpServletRequest request) throws Exception {

        log.info("{}.getUserIdExists Start!",this.getClass().getName());

        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("userId : {}",userId);

        UsersDTO pDTO = new UsersDTO();
        pDTO.setUserid(userId);

        UsersDTO rDTO = Optional.ofNullable(usersService.getUserIdExists(pDTO)).orElseGet(UsersDTO::new);

        log.info("{}.getUserIdExists End!",this.getClass().getName());
        return rDTO;
    }
    @ResponseBody
    @PostMapping(value = "getEmailExists")
    public UsersDTO getEmailExists(HttpServletRequest request) throws Exception{
        log.info("{}.getEmailExists Start", this.getClass().getName());

        String email = CmmUtil.nvl(request.getParameter("email"));

        log.info("email : {}", email);

        UsersDTO pDTO = new UsersDTO();
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        UsersDTO rDTO = Optional.ofNullable(usersService.getEmailExists(pDTO)).orElseGet(UsersDTO::new);

        log.info("{}.getEmailExists End!", this.getClass().getName());

        return rDTO;
    }
    @ResponseBody
    @PostMapping(value = "insertUsers")
    public MsgDTO insertUsers(HttpServletRequest request) {

        log.info("{}.insertUsers start!", this.getClass().getName());

        int res = 0;
        String msg = "";
        MsgDTO dto;

        UsersDTO pDTO;

        try {

            // 회원가입 정보
            String loginId = CmmUtil.nvl(request.getParameter("loginId"));
            String userName = CmmUtil.nvl(request.getParameter("userName"));
            String password = CmmUtil.nvl(request.getParameter("password"));
            String phone = CmmUtil.nvl(request.getParameter("phone"));
            String email = CmmUtil.nvl(request.getParameter("email"));

            log.info("loginId : " + loginId);
            log.info("userName : " + userName);
            log.info("phone : " + phone);
            log.info("email : " + email);

            pDTO = new UsersDTO();

            // DB의 userId는 AUTO_INCREMENT이므로 넣지 않음

            pDTO.setLoginId(loginId);
            pDTO.setUserName(userName);

            // 비밀번호는 복호화할 수 없도록 해시 처리
            pDTO.setPassword(
                    EncryptUtil.encHashSHA256(password)
            );

            pDTO.setPhone(phone);

            // 이메일 암호화
            pDTO.setEmail(
                    EncryptUtil.encAES128CBC(email)
            );

            // 일반 사용자 권한
            pDTO.setUserRole("USER");

            res = usersService.insertUsers(pDTO);

            log.info("회원가입 결과 (res) : " + res);

            if (res == 1) {

                msg = "회원가입되었습니다.";

            } else if (res == 2) {

                msg = "이미 가입된 아이디입니다.";

            } else {

                msg = "오류로 인해 회원가입이 실패했습니다.";
            }

        } catch (Exception e) {

            msg = "실패하였습니다.";
            log.error("회원가입 오류", e);

        } finally {

            dto = new MsgDTO();

            dto.setResult(res);
            dto.setMsg(msg);

            log.info("{}.insertUsers End!", this.getClass().getName());
        }

        return dto;
    }




}
