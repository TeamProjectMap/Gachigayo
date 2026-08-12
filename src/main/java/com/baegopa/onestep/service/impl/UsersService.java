package com.baegopa.onestep.service.impl;

import com.baegopa.onestep.dto.MailDTO;
import com.baegopa.onestep.dto.UsersDTO;
import com.baegopa.onestep.mapper.IUsersMapper;
import com.baegopa.onestep.service.IMailService;
import com.baegopa.onestep.service.IUsersService;
import com.baegopa.onestep.util.CmmUtil;
import com.baegopa.onestep.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService implements IUsersService {
    private final IUsersMapper usersMapper;

    private final IMailService mailService;

    @Override
    public UsersDTO getUserIdExists(UsersDTO pDTO) throws Exception {
        log.info("{}getUserIdExists Start!", this.getClass().getName());
        UsersDTO rDTO = usersMapper.getUserIdExists(pDTO);

        log.info("{}.getUserIdExosts End!", this.getClass().getName());

        return rDTO;
    }

    @Override
    public UsersDTO getEmailExists(UsersDTO pDTO) throws Exception {
        log.info("{}.emailAuth Start!",this.getClass().getName());
        UsersDTO rDTO = Optional.ofNullable(usersMapper.getEmailExists(pDTO)).orElseGet(UsersDTO::new);

        log.info("rDTO : {}", rDTO);
        if (CmmUtil.nvl(rDTO.getExistsYn()).equals("N")) {

            int authNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);

            log.info("authNumber : {}", authNumber);

            MailDTO dto = new MailDTO();

            dto.setTitle("이메일 중복 확인 인증번호 발송 메일");
            dto.setContents("인증번호는 " + authNumber + " 입니다.");
            dto.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.getEmail())));

            mailService.doSendMail(dto);

            dto = null;

            rDTO.setAuthNumber(authNumber);
        }

        log.info("{}.emailAuth End!", this.getClass().getName());

        return rDTO;
    }

    @Override
    public int insertUsers(UsersDTO pDTO) throws Exception {
        log.info("{}.insertUsers Start!",this.getClass().getName());

        int res;

        int success = usersMapper.insertUsers(pDTO);

        if (success > 0) {
            res = 1;

            MailDTO mDTO = new MailDTO();

            mDTO.setToMail(EncryptUtil.decAES128CBC(CmmUtil.nvl(pDTO.getEmail())));

            mDTO.setTitle("회원가입을 축하드립니다.");

            mDTO.setContents(CmmUtil.nvl(pDTO.getUserName()) + "님의 회원가입을 진심으로 축하드립니다.");

            mailService.doSendMail(mDTO);
        }else {
            res = 0;
        }
        log.info("{}.insertUsers End!",this.getClass().getName());

        return res;

    }

}

