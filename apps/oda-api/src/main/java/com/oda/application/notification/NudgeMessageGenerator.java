package com.oda.application.notification;

public class NudgeMessageGenerator {

    public static String generateTitle(String eventTitle, int daysUntil) {
        if (daysUntil == 0) return eventTitle + " - 오늘이 마감일입니다!";
        if (daysUntil <= 1) return eventTitle + " - 내일 마감!";
        return eventTitle + " - D-" + daysUntil;
    }

    public static String generateBody(String eventTitle, int daysUntil, String actionUrl) {
        if (daysUntil <= 1) return "놓치면 다음 기회는 6개월 뒤입니다. 지금 바로 신청하세요!";
        if (daysUntil <= 3) return eventTitle + " 마감 " + daysUntil + "일 전입니다. 필요 서류를 확인하세요.";
        return eventTitle + " 마감 " + daysUntil + "일 전입니다.";
    }
}
