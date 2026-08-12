package com.baegopa.onestep.service.impl;

import com.baegopa.onestep.dto.MailDTO;
import com.baegopa.onestep.service.IMailService;
import com.baegopa.onestep.util.CmmUtil;

import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public int doSendMail(MailDTO pDTO) {

        log.info("{}.doSendMail start!",
                this.getClass().getName());

        // 메일 발송 성공 여부
        int res = 1;

        // DTO가 null이면 발송 실패
        if (pDTO == null) {
            log.error("MailDTO is null");
            return 0;
        }

        // DTO에서 데이터 가져오기
        String toMail = CmmUtil.nvl(pDTO.getToMail());
        String title = CmmUtil.nvl(pDTO.getTitle());
        String contents = CmmUtil.nvl(pDTO.getContents());

        log.info(
                "toMail : {} / title : {} / content : {}",
                toMail,
                title,
                contents
        );

        try {

            // 메일 메시지 생성
            MimeMessage message =
                    mailSender.createMimeMessage();

            // 메일 메시지 생성 도우미
            MimeMessageHelper messageHelper =
                    new MimeMessageHelper(message, "UTF-8");

            // 받는 사람
            messageHelper.setTo(toMail);

            // 보내는 사람
            messageHelper.setFrom(fromMail);

            // 메일 제목
            messageHelper.setSubject(title);

            // 메일 내용
            messageHelper.setText(contents);

            // 메일 발송
            mailSender.send(message);

        } catch (Exception e) {

            res = 0;

            log.error(
                    "[ERROR] doSendMail : {}",
                    e.getMessage(),
                    e
            );
        }

        log.info("{}.doSendMail end!",
                this.getClass().getName());

        return res;
    }
}