package io.github.aliontory.uni_meeting.services;

import io.github.aliontory.uni_meeting.beans.vo.SchoolVO;

import java.util.List;

public interface SchoolService {
    List<SchoolVO> getList();

    SchoolVO get(String schoolId);

    List<String> getIdList();
}
