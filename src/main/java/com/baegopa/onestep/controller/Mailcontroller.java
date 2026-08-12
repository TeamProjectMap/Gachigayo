package com.baegopa.onestep.controller;

import com.baegopa.onestep.dto.MailDTO;
import com.baegopa.onestep.dto.MsgDTO;
import com.baegopa.onestep.service.IMailService;
import com.baegopa.onestep.util.CmmUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping(value = "/mail")
@RequiredArgsConstructor
@Controller
public class Mailcontroller {
    private final IMailService mailService;//매일 발송을 위한 서비스 객체사용

    @GetMapping(value = "mailForm")
    public String mailForm() {

        log.info("{}.mailForm Start!",this.getClass().getName());

        return  "mail/mailForm";

    }
    @ResponseBody
    @PostMapping(value = "sendMail")
    public MsgDTO sendMail(HttpServletRequest request) {
        log.info("{}.sendMail Start!",this.getClass().getName());

        String msg;

        String toMail = CmmUtil.nvl(request.getParameter("toMail")); // 받는사람
        String title = CmmUtil.nvl(request.getParameter("title")); // 제목
        String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용


        log.info("toMail : {} / title : {} / contents : {}", toMail, title, contents);

        MailDTO pDTO = new MailDTO();

        pDTO.setToMail(toMail); //
        pDTO.setTitle(title); //
        pDTO.setContents(contents); //

        int res = mailService.doSendMail(pDTO);

        if (res == 1) {
            msg = "메일 발송하였습니다.";

        } else {
            msg = "메일 발송 실패하였습니다.";
        }

        log.info(msg);


        MsgDTO dto = new MsgDTO();
        dto.setMsg(msg);


        log.info("{}.sendMail End!", this.getClass().getName());

        return dto;
    }
}



