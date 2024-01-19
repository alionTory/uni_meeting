package io.github.aliontory.uni_meeting.beans.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SchoolVO {
    private String schoolId;
    private String schoolNameKor;
    private String regdate;
    private String updatedate;
}
