package io.github.aliontory.uni_meeting.beans.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

// 장소 ID를 통해 카카오맵에서 장소 정보를 가져옴
@Component
@Data
public class PlaceVO {
    private String placeName;
    private String regionAddr;
    private String streetAddr;
    private String pathFindUrl;
    private double x;
    private double y;
}
