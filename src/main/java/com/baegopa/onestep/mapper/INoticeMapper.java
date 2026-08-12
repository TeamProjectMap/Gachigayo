package com.baegopa.onestep.mapper;

import com.baegopa.onestep.dto.NotificationDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface INoticeMapper {
    List<NotificationDTO> getNoticeList() throws Exception;

    void insertNoticeInfo(NotificationDTO pDto) throws  Exception;

    NotificationDTO getNoticeInfo(NotificationDTO pDto) throws Exception;

    void updateNoticeReadCnt(NotificationDTO pDto) throws Exception;

    void updateNoticeInfo(NotificationDTO pDto) throws Exception;

    void deleteNoticeInfo(NotificationDTO pDto) throws Exception;
}
