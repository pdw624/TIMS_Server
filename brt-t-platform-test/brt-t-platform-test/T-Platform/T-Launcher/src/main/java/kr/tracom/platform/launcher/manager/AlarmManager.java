package kr.tracom.platform.launcher.manager;


import kr.tracom.platform.service.dao.PlatformDao;
import kr.tracom.platform.service.dao.PlatformMapper;
import kr.tracom.platform.service.domain.MtAdmin;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class AlarmManager {
    private static final String ADM_MAIL_ID = "waybeasdasdll81@gmail.com";
    private static final String ADM_MAIL_PW = "rudnasdasf81*!";

    private static Date lastSendedDatetime = null;
    private static final long retryTimeout = 1000 * 60 * 1;

    public static void SendToAdmin() {
        if(lastSendedDatetime != null) {
            Date nowDate = new Date();
            long elapsedSeconds = (nowDate.getTime() - lastSendedDatetime.getTime()) / 1000L;
            lastSendedDatetime = nowDate;

            if (elapsedSeconds < retryTimeout) {
                return;
            }
        }

        PlatformDao platformDao = new PlatformDao();
        List<Object> list = platformDao.selectList(PlatformMapper.ADMIN_SELECT, null);

        // TODO : 향후 플랫폼 DB에서 관리자 메일 주소 얻어와 메일 전송을 해야한다.
        MtAdmin admin;
        for(Object obj : list) {
            admin = (MtAdmin)obj;

            System.out.println(admin);

            SendGmail("waybell@naver.com", "테스트 메일", "테스트 내용");
        }
    }

    private static void SendGmail(String user, String subject, String content) {
        Properties p = System.getProperties();
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.host", "smtp.gmail.com");      // smtp 서버 호스트
        p.put("mail.smtp.auth","true");
        p.put("mail.smtp.port", "587");                 // gmail 포트

        //session 생성 및  MimeMessage생성
        Session session = Session.getDefaultInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String id = "waybell81@gmail.com";       // 구글 ID
                String pw = "rudnf81*!";

                return new PasswordAuthentication(id, pw);
            }
        });
        MimeMessage msg = new MimeMessage(session);
        String charSet = "UTF-8";

        try{
            // 편지보낸시간 설정
            msg.setSentDate(new Date());

            // 송신자 설정
            //InternetAddress from = new InternetAddress(new String(fromName.getBytes(charSet), "8859_1") + "<waybell81@gmail.com>");
            InternetAddress from = new InternetAddress("SongGJ<waybell81@gmail.com>");
            msg.setFrom(from);

            // 수신자 설정
            InternetAddress to = new InternetAddress(user);
            msg.setRecipient(Message.RecipientType.TO, to);

            // 제목 설정

            msg.setSubject(subject, "UTF-8");
            msg.setText(content, "UTF-8");

            msg.setHeader("content-Type", "text/html");

            System.out.println(msg);

            // 메일 송신
            Transport.send(msg);

            System.out.println("메일 발송을 완료하였습니다.");
        }catch (AddressException addr_e) {  //예외처리 주소를 입력하지 않을 경우
            JOptionPane.showMessageDialog(null, "메일을 입력해주세요", "메일주소입력", JOptionPane.ERROR_MESSAGE);
            addr_e.printStackTrace();
        }catch (MessagingException msg_e) { //메시지에 이상이 있을 경우
            JOptionPane.showMessageDialog(null, "메일을 제대로 입력해주세요.", "오류발생", JOptionPane.ERROR_MESSAGE);
            msg_e.printStackTrace();
        }
    }
}
