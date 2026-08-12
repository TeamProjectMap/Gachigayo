package com.baegopa.onestep.service;

import com.baegopa.onestep.dto.NotificationDTO;

import java.util.List;

public interface INoticeService {
    List<NotificationDTO> getNoticeList() throws Exception;

    NotificationDTO getNoticeInfo(NotificationDTO pDTO, boolean type) throws Exception;

    void insertNoticeInfo(NotificationDTO pDTO) throws Exception;

    void updateNoticeInfo(NotificationDTO pDTO) throws Exception;

    void deleteNoticeInfo(NotificationDTO pDTO) throws Exception;
}
