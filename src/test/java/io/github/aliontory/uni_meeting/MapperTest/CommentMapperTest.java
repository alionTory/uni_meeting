package io.github.aliontory.uni_meeting.MapperTest;

import io.github.aliontory.uni_meeting.beans.vo.CommentVO;
import io.github.aliontory.uni_meeting.mapper.CommentMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class CommentMapperTest {
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void commentsInsertTest() {
        Random rand = new Random();
        IntStream.rangeClosed(1, 30).forEach(i -> {
            Long bno = 607L + rand.nextInt(5);
            Long mid = 1L + rand.nextInt(10);
            String content = "댓글내용" + i;

            CommentVO commentVO = new CommentVO();
            commentVO.setBno(bno);
            commentVO.setMid(mid);
            commentVO.setCommentContent(content);

            Long cno = commentMapper.insert(commentVO);

            int subComCount = rand.nextInt(6);
            Set<Long> commenterSet = new HashSet<>();
            commenterSet.add(mid);
            for (int j = 1; j <= subComCount; j++) {
                List<Long> commenterList = commenterSet.stream().toList();
                Long targetMid = commenterList.get(rand.nextInt(commenterSet.size()));

                Long subMid = 1L + rand.nextInt(10);
                commenterSet.add(subMid);
                String subContent = "답글내용" + j;

                CommentVO subCommentVO = new CommentVO();
                subCommentVO.setBno(bno);
                subCommentVO.setMid(subMid);
                subCommentVO.setCommentContent(subContent);
                subCommentVO.setTargetCno(cno);
                subCommentVO.setTargetMid(targetMid);
                commentMapper.insert(subCommentVO);
            }
        });
    }

    @Test
    public void commentDeleteTest(){
        commentMapper.delete(49L);
    }

}
