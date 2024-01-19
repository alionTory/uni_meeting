package io.github.aliontory.uni_meeting.beans.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
public class PageDTO {
    private int startPage;
    private int endPage;
    private int realEnd;
    private boolean prev, next;

    private int total;  // 전체 게시물 개수
    private Criteria cri;

    public PageDTO() {
    }

    public PageDTO(Criteria cri, int total) {
        this.cri = cri;
        this.total = total;

        // realEnd는 정말 마지막 페이지가 되는 페이지 번호
        realEnd = (int) Math.ceil((total * 1.0) / cri.getAmount());

        if (cri.getPageNum() > this.realEnd)
            cri.setPageNum(this.realEnd);

        // endPage는 현재 페이지가 1~10이라면 10
        // endPage는 현재 페이지가 21이라면 30이 나와야 함 => Math.ceil 필요
        endPage = (int) Math.ceil(cri.getPageNum() / 10.0) * 10;

        // startPage는 endPage가 10이면 1, 20이면 11, 30이면 21
        startPage = endPage - 9;


        log.info("-".repeat(20) + " endPage : " + endPage);
        log.info("-".repeat(20) + " startPage : " + startPage);
        log.info("-".repeat(20) + " realEnd : " + realEnd);

        if (realEnd < this.endPage) {
            endPage = (realEnd == 0) ? 1 : realEnd;
            // 글이 한 개도 없어서 realEnd가 0이 되는 경우에도 endPage는 1이어야 함
        }


        prev = startPage > 1;
        next = endPage < realEnd;

        log.info("-".repeat(20) + "prev : " + prev);
        log.info("-".repeat(20) + "next : " + next);
    }
}
