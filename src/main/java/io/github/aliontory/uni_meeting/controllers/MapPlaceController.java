package io.github.aliontory.uni_meeting.controllers;

import io.github.aliontory.uni_meeting.beans.vo.PlaceVO;
import io.github.aliontory.uni_meeting.services.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
public class MapPlaceController {
    private final BoardService boardService;

    @GetMapping("/getPlace/{placeId}")
    public PlaceVO getPlace(@PathVariable("placeId") long placeId) {
        PlaceVO placeVO = boardService.getPlaceFromKakao(placeId);
        return placeVO;
    }
}
