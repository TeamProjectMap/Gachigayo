package com.baegopa.onestep.service;

import com.baegopa.onestep.dto.MailDTO;

public interface IMailService {
    int doSendMail(MailDTO pDTO);
}
