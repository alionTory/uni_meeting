package io.github.aliontory.uni_meeting.mapper;

import io.github.aliontory.uni_meeting.beans.vo.SchoolVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Mapper
public interface SchoolMapper {
    List<SchoolVO> getList();
    SchoolVO get(String schoolId);
    List<String> getIdList();
}
