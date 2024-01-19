package io.github.aliontory.uni_meeting.beans.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommentVO {
    private long cno;
    private long bno;
    private long mid;
    private String commentWriterName;
    private String commentContent;
    private Long targetCno;  // 답글 대상
    private Long targetMid;  // 답글 대상 작성자 번호
    private String targetUserName;  // 답글 대상 작성자 이름
    private boolean edited;
    private boolean secret;
    private boolean del;  // 답글이 달린 댓글은 그냥 삭제하지 않고 del을 true로 설정
    private String regdate;
    private String updatedate;
}
