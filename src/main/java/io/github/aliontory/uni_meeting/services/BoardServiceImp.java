package io.github.aliontory.uni_meeting.services;

import io.github.aliontory.uni_meeting.beans.vo.BoardVO;
import io.github.aliontory.uni_meeting.beans.vo.Criteria;
import io.github.aliontory.uni_meeting.beans.vo.PlaceVO;
import io.github.aliontory.uni_meeting.dao.BoardDAO;
import io.github.aliontory.uni_meeting.dao.CommentDAO;
import io.github.aliontory.uni_meeting.security.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/*
@Preauthorize 등 사용할 때 주의 :
스프링은 proxy로 bean에 등록된 객체를 감싼다.
Preauthorize를 통한 권한 처리도 proxy를 통해 이루어짐.
의존성 주입을 해야만 proxy를 얻는다.
따라서 클래스 내부 메소드를 호출하면 preauthorize등이 적용 안됨.

되도록이면 호출 구조를 바꿔서 proxy로부터 메소드가 실행되도록 할 것.

또는
private final ObjectProvider<주입받을 클래스명> objectProvider;
objectProvider.getObject().내부메서드명();
와 같이 ObjectProvider를 사용하거나

setter을 통해 의존성 주입
(생성자를 통한 주입은 순환 주입이 안 되서 불가)
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class BoardServiceImp implements BoardService {
    private final BoardDAO boardDAO;
    private final CommentDAO commentDAO;
    private final WebClient webClient;

    @Override
    public int getTotal(Criteria cri) {
        return boardDAO.getTotal(cri);
    }

    @Override
    @PreAuthorize("principal.mid==#boardVO.mid")
    public void register(BoardVO boardVO) {
        if (!StringUtils.hasText(boardVO.getTitle()) || !StringUtils.hasText(boardVO.getContent())) {
            throw new RuntimeException("게시글의 제목 또는 내용이 비어 있습니다.");
        }

        boardDAO.register(boardVO);
    }

    @Override
    public List<BoardVO> getList(Criteria cri) {
        return boardDAO.getList(cri);
    }

    @Override
    public BoardVO get(Long bno, String schoolName, int boardId) {
        return boardDAO.get(bno, schoolName, boardId);
    }

    @Override
    public BoardVO getByBno(Long bno) {
        return boardDAO.getByBno(bno);
    }

    @Override
    public boolean remove(Long bno, String schoolName, int boardId) {
        log.info("trying to remove");
        BoardVO boardVO = get(bno, schoolName, boardId);
        MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 메서드 중간에서 권한 처리
        if (currentMember.hasAuthorities("ADMIN") || currentMember.getMid() == boardVO.getMid()) {
            boolean result = boardDAO.remove(boardVO.getBno(), boardVO.getSchoolName(), boardVO.getBoardId());
            if (result)  // 게시글 삭제에 성공하면 댓글들도 삭제
                commentDAO.deleteByBno(bno);
            return result;
        } else {
            throw new AccessDeniedException("403");
        }
    }

    @Override
    public boolean modify(BoardVO boardVO) {
        if (!StringUtils.hasText(boardVO.getTitle()) || !StringUtils.hasText(boardVO.getContent())) {
            throw new RuntimeException("게시글의 제목 또는 내용이 비어 있습니다.");
        }

        BoardVO boardVODb = get(boardVO.getBno(), boardVO.getSchoolName(), boardVO.getBoardId());
        MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 메서드 중간에서 권한 처리
        if (currentMember.getMid() == boardVODb.getMid()) {
            return boardDAO.modify(boardVO);
        } else {
            throw new AccessDeniedException("403");
        }
    }

    @Override
    public PlaceVO getPlaceFromKakao(long placeId) {
        log.info("카카오로부터 장소 정보 가져오기 시작");
        Mono<String> jsonMono = webClient.get().uri(Long.toString(placeId))
                .accept(MediaType.APPLICATION_JSON).retrieve()
                .bodyToMono(String.class);
        String jsonString = jsonMono.block();

        log.info("가져온 장소 정보");
        log.info(jsonString);

        PlaceVO placeVO = parsePlaceJson(jsonString);
        placeVO.setPathFindUrl("https://map.kakao.com/link/to/" + placeId);
        return placeVO;
    }

    private PlaceVO parsePlaceJson(String jsonString) {
        PlaceVO placeVO = new PlaceVO();
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(jsonString);

            JSONObject basicInfo = (JSONObject) jsonObject.get("basicInfo");

            long x = (long) basicInfo.get("wpointx");
            long y = (long) basicInfo.get("wpointy");

            String placeName = (String) basicInfo.get("placenamefull");
            JSONObject address = (JSONObject) basicInfo.get("address");
            JSONObject newaddr = (JSONObject) address.get("newaddr");
            JSONObject region = (JSONObject) address.get("region");
            String addrbunho = (String) address.get("addrbunho");

            String regionDetail = (String) region.get("name3");
            String regionOuter = (String) region.get("newaddrfullname");

            String newaddrDetail = null;
            if (newaddr != null) {
                newaddrDetail = (String) newaddr.get("newaddrfull");
                placeVO.setStreetAddr(regionOuter + " " + newaddrDetail);
            }

            placeVO.setPlaceName(placeName);
            placeVO.setRegionAddr(regionOuter + " " + regionDetail + " " + addrbunho);
            placeVO.setX(x);
            placeVO.setY(y);

            return placeVO;
        } catch (NullPointerException e) {
            log.info("카카오 json 파싱 중 필요한 값을 찾지 못했습니다.");
            log.info("NullPointerException");
            return null;
        } catch (ParseException e) {
            log.info("json 파싱 중 에러 발생");
            log.info("PlaceVO 객체 생성 불가");
            return null;
        }
    }
}
