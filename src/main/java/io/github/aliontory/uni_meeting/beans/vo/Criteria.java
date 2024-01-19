package io.github.aliontory.uni_meeting.beans.vo;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Data
@Log4j2
public class Criteria {
    private int pageNum;  // 현재 몇 번째 페이지인가
    private int amount;  // 한 페이지에 보여줄 게시글의 개수
    private int limit;
    private int offset;
    private String schoolName;
    private int boardId;

    // 검색 기능을 위한 field
    private String type;
    private String keyword;

    public Criteria(){
        this("", 0, 1,20);
    }

    public Criteria(String schoolName, int boardId, int pageNum, int amount) {
        this.schoolName = schoolName;
        this.boardId = boardId;
        if (pageNum < 1)
            pageNum = 1;
        this.pageNum = pageNum;
        this.amount = amount;
        this.limit = amount;
        this.offset = (pageNum - 1) * amount;
    }

    public void setParam() {
        this.limit = amount;
        this.offset = (pageNum - 1) * amount;
    }

    // Type을 가지고 한 글자씩 분리
    public String[] getTypeArr() {
        // String.split("") : 모든 문자가 분리 "ABC".split("") => "A" "B" "C"
        return type == null ? new String[]{} : type.split("");
    }

    public void setPageNum(int pageNum) {
        if (pageNum < 1)
            pageNum = 1;
        this.pageNum = pageNum;
    }
}
