package com.baegopa.onestep.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDTO {
    private Long notificationId;  // 알림 번호
    private Long receiverId;      // 알림을 받는 사용자 ID
    private Long tripEventId;     // 여행 이벤트 ID
    private String notifyType;    // 알림 종류
    private String content;       // 알림 내용
    private String regDt;         // 등록일
}

