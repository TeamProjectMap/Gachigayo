package com.baegopa.onestep.controller;

import com.baegopa.onestep.dto.MsgDTO;
import com.baegopa.onestep.dto.NotificationDTO;
import com.baegopa.onestep.service.INoticeService;
import com.baegopa.onestep.util.CmmUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/notice")
@RequiredArgsConstructor
@Controller
public class NoticeController {
    private final INoticeService noticeService;

    @GetMapping(value = "noticeList")
    public String noticeList(HttpSession session, ModelMap model)
            throws Exception {

        log.info("{}.noticeList Start!", this.getClass().getName());

        session.setAttribute("SESSION_USER_ID", "USER01");

        List<NotificationDTO> rList =
                Optional.ofNullable(noticeService.getNoticeList())
                        .orElseGet(ArrayList::new);

        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".noticeList End!");

        return "notice/noticeList";
    }


    @GetMapping(value = "noticeReg")
    public String noticeReg() {

        log.info("{}.noticeReq Start!", this.getClass().getName());

        log.info("{}.noticeReg End!", this.getClass().getName());

        return "notice/noticeReg";
    }


    @ResponseBody
    @PostMapping(value = "noticeInsert")
    public MsgDTO noticeInsert(
            HttpServletRequest request,
            HttpSession session) {

        log.info("{}.noticeInsert Start!",
                this.getClass().getName());

        String msg = "";
        MsgDTO dto;

        try {

            String receiverId = CmmUtil.nvl(
                    request.getParameter("receiverId"));

            String tripEventId = CmmUtil.nvl(
                    request.getParameter("tripEventId"));

            String notifyType = CmmUtil.nvl(
                    request.getParameter("notifyType"));

            String content = CmmUtil.nvl(
                    request.getParameter("content"));


            log.info(
                    "receiverId : {} / tripEventId : {} / notifyType : {} / content : {}",
                    receiverId,
                    tripEventId,
                    notifyType,
                    content
            );


            NotificationDTO pDTO = new NotificationDTO();

            // DB가 bigint이므로 Long으로 변환
            pDTO.setReceiverId(Long.parseLong(receiverId));
            pDTO.setTripEventId(Long.parseLong(tripEventId));

            pDTO.setNotifyType(notifyType);
            pDTO.setContent(content);


            noticeService.insertNoticeInfo(pDTO);

            msg = "등록되었습니다.!";

        } catch (Exception e) {

            msg = "실패하였습니다. : " + e.getMessage();

            log.info(e.toString());

        } finally {

            dto = new MsgDTO();

            dto.setMsg(msg);

            log.info("{}.noticeInsert End!",
                    this.getClass().getName());
        }

        return dto;
    }


    @GetMapping(value = "noticeInfo")
    public String noticeInfo(
            HttpServletRequest request,
            ModelMap model) throws Exception {

        log.info("{}.noticeInfo Start!",
                this.getClass().getName());


        String notificationId =
                CmmUtil.nvl(request.getParameter("notificationId"));

        log.info("notificationId : {}", notificationId);


        NotificationDTO pDTO = new NotificationDTO();

        // notificationId는 bigint
        pDTO.setNotificationId(
                Long.parseLong(notificationId)
        );


        NotificationDTO rDTO =
                Optional.ofNullable(
                        noticeService.getNoticeInfo(pDTO, true)
                ).orElseGet(NotificationDTO::new);


        model.addAttribute("rDTO", rDTO);


        log.info(this.getClass().getName()
                + ".noticeInfo End!");


        return "notice/noticeInfo";
    }


    @GetMapping(value = "noticeEditInfo")
    public String noticeEditInfo(
            HttpServletRequest request,
            ModelMap model) throws Exception {

        log.info("{}.noticeEditInfo Start!",
                this.getClass().getName());


        String notificationId =
                CmmUtil.nvl(
                        request.getParameter("notificationId")
                );

        log.info("notificationId : {}", notificationId);


        NotificationDTO pDTO = new NotificationDTO();

        pDTO.setNotificationId(
                Long.parseLong(notificationId)
        );


        NotificationDTO rDTO =
                Optional.ofNullable(
                        noticeService.getNoticeInfo(pDTO, false)
                ).orElseGet(NotificationDTO::new);


        model.addAttribute("rDTO", rDTO);


        log.info("{}.noticeEditInfo End!",
                this.getClass().getName());

        return "notice/noticeEditInfo";
    }


    @ResponseBody
    @PostMapping(value = "noticeUpdate")
    public MsgDTO noticeUpdate(
            HttpSession session,
            HttpServletRequest request) {

        log.info("{}.noticeUpdate Start!",
                this.getClass().getName());

        String msg = "";
        MsgDTO dto;

        try {

            String notificationId =
                    CmmUtil.nvl(
                            request.getParameter("notificationId")
                    );

            String receiverId =
                    CmmUtil.nvl(
                            request.getParameter("receiverId")
                    );

            String tripEventId =
                    CmmUtil.nvl(
                            request.getParameter("tripEventId")
                    );

            String notifyType =
                    CmmUtil.nvl(
                            request.getParameter("notifyType")
                    );

            String content =
                    CmmUtil.nvl(
                            request.getParameter("content")
                    );


            log.info(
                    "notificationId : {} / receiverId : {} / tripEventId : {} / notifyType : {} / content : {}",
                    notificationId,
                    receiverId,
                    tripEventId,
                    notifyType,
                    content
            );


            NotificationDTO pDTO = new NotificationDTO();

            pDTO.setNotificationId(
                    Long.parseLong(notificationId)
            );

            pDTO.setReceiverId(
                    Long.parseLong(receiverId)
            );

            pDTO.setTripEventId(
                    Long.parseLong(tripEventId)
            );

            pDTO.setNotifyType(notifyType);
            pDTO.setContent(content);


            noticeService.updateNoticeInfo(pDTO);

            msg = "수정되었습니다.!";

        } catch (Exception e) {

            msg = "실패하였습니다. : " + e.getMessage();

            log.info(e.toString());

        } finally {

            dto = new MsgDTO();

            dto.setMsg(msg);

            log.info("{}.noticeUpdate End!",
                    this.getClass().getName());
        }

        return dto;
    }


    @ResponseBody
    @PostMapping(value = "noticeDelete")
    public MsgDTO noticeDelete(
            HttpSession session,
            HttpServletRequest request) {

        log.info("{}.noticeDelete Start!",
                this.getClass().getName());

        String msg = "";
        MsgDTO dto;

        try {

            String notificationId =
                    CmmUtil.nvl(
                            request.getParameter("notificationId")
                    );

            log.info("notificationId : {}", notificationId);


            NotificationDTO pDTO = new NotificationDTO();

            pDTO.setNotificationId(
                    Long.parseLong(notificationId)
            );


            noticeService.deleteNoticeInfo(pDTO);

            msg = "성공했습니다.";

        } catch (Exception e) {

            msg = "실패하였습니다. :" + e.getMessage();

            log.info(e.toString());

        } finally {

            dto = new MsgDTO();

            dto.setMsg(msg);

            log.info("{}.noticeDelete End!",
                    this.getClass().getName());

        }

        return dto;
    }
}