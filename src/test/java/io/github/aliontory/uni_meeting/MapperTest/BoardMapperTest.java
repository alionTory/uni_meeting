package io.github.aliontory.uni_meeting.MapperTest;

import io.github.aliontory.uni_meeting.beans.vo.BoardVO;
import io.github.aliontory.uni_meeting.beans.vo.Criteria;
import io.github.aliontory.uni_meeting.mapper.BoardMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class BoardMapperTest {
    @Autowired
    private BoardMapper boardMapper;

    @Test
    public void insertTest() {
        for (int i = 1; i <= 30; i++) {
            BoardVO vo = new BoardVO();
            vo.setTitle("더미식사게시판제목" + i);
            vo.setContent("더미식사게시판내용" + i + "\nadsjflkasjfklsajdfkjaslkdfjkalsjflsdfaklsjdflkajsdfjaf\nasdkfjsalkjfklasjfdlkajsdkljfalsjfsdjakldsjfklasjdfl");
            vo.setMid(i % 10 + 1);
            vo.setSchoolName("konkuk");
            vo.setBoardId(3);
            //vo.setWriter("더미작성자"+i);
            //vo.setBoardType("hobby");
            boardMapper.insert(vo);
        }
    }


    @Test
    public void getListTest() {
        Criteria cri = new Criteria();
        cri.setAmount(10);
        cri.setPageNum(5);
        cri.setParam();
        //cri.setBoardType("hobby");

        List<BoardVO> list = boardMapper.getList(cri);
        log.info(list.toString());
    }

    @Test
    public void testTest() {
        log.info("test");
    }
}
