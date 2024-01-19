package io.github.aliontory.uni_meeting.beans.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BoardVO {
    private long bno;
    private String title;
    private String content;
    private long mid;
    private String writerName;
    private String regdate;
    private String updatedate;
    private String schoolName;
    private int boardId;
    private boolean edited;
    private Long placeId;

    public static String getBoardTitle(int boardId) {
        return switch (boardId) {
            case 1 -> "관심사 · 취미 관련 모임 게시판";
            case 2 -> "재능교류 · 스터디";
            case 3 -> "같이 밥 먹을 사람";
            default -> null;
        };
    }
}
