package io.github.aliontory.uni_meeting.services;

import io.github.aliontory.uni_meeting.beans.vo.SchoolVO;
import io.github.aliontory.uni_meeting.mapper.SchoolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolServiceImp implements SchoolService {
    private final SchoolMapper schoolMapper;

    @Override
    public List<SchoolVO> getList() {
        return schoolMapper.getList();
    }

    @Override
    public SchoolVO get(String schoolId) {
        return schoolMapper.get(schoolId);
    }

    @Override
    public List<String> getIdList() {
        return schoolMapper.getIdList();
    }
}
