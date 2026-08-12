package com.baegopa.onestep.service.impl;

import com.baegopa.onestep.dto.NotificationDTO;
import com.baegopa.onestep.mapper.INoticeMapper;
import com.baegopa.onestep.service.INoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NoticeService implements INoticeService {

    private final INoticeMapper noticeMapper;

    @Override
    public List<NotificationDTO> getNoticeList() throws Exception {
        log.info("{}.getNoticeList start!",this.getClass().getName());

        return noticeMapper.getNoticeList();
    }
    @Transactional
    @Override
    public NotificationDTO getNoticeInfo(NotificationDTO pDTO, boolean type) throws Exception {
        log.info("{}.getNoticeInfo start!",this.getClass().getName());

        if (type) {
            log.info("Update ReadCNT");
            noticeMapper.updateNoticeReadCnt(pDTO);
        }

        return noticeMapper.getNoticeInfo(pDTO);

    }
    @Transactional
    @Override
    public void insertNoticeInfo(NotificationDTO pDTO) throws Exception {
        log.info("{}.InsertNoticeInfo start!",this.getClass().getName());
        noticeMapper.insertNoticeInfo(pDTO);
    }
    @Transactional
    @Override
    public void updateNoticeInfo(NotificationDTO pDTO) throws Exception {
        log.info("{}.updateNoticeInfo start!",this.getClass().getName());
        noticeMapper.updateNoticeInfo(pDTO);
    }
    @Transactional
    @Override
    public void deleteNoticeInfo(NotificationDTO pDTO) throws Exception {
        log.info("{}.deleteNoticeInfo start!", this.getClass().getName());
        noticeMapper.deleteNoticeInfo(pDTO);

    }
}
