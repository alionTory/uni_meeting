package io.github.aliontory.uni_meeting.services;

import io.github.aliontory.uni_meeting.beans.vo.CommentVO;
import io.github.aliontory.uni_meeting.dao.CommentDAO;
import io.github.aliontory.uni_meeting.domain.Member;
import io.github.aliontory.uni_meeting.repository.MemberRepository;
import io.github.aliontory.uni_meeting.security.MemberSecurityDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommentServiceImp implements CommentService {
    private final CommentDAO commentDAO;
    private final MemberRepository memberRepository;

    // boardMid에는 반드시 DB로부터의 값이 들어가야 함.
    @Override
    public List<List<CommentVO>> getListByBno(Long bno, Long boardMid) {
        List<CommentVO> originalList = commentDAO.getListByBno(bno);

        addWriterNameToList(originalList);

        final Map<Long, List<CommentVO>> commentMap = originalList.stream()
                .filter(commentVO -> commentVO.getTargetCno() == null)
                .collect(Collectors.toMap(commentVO -> commentVO.getCno(), commentVO -> {
                    List<CommentVO> innerList = new ArrayList<>();
                    innerList.add(commentVO);
                    return innerList;
                }));

        originalList.stream()
                .filter(commentVO -> commentVO.getTargetCno() != null)
                .forEach(commentVO -> {
                    List<CommentVO> innerList = commentMap.get(commentVO.getTargetCno());

                    if (innerList != null)
                        innerList.add(commentVO);
                    else
                        log.info("답글 처리 과정에서 원 댓글을 찾지 못했습니다.");
                });

        List<Long> keyList = commentMap.keySet().stream().sorted().toList();
        List<List<CommentVO>> reformedList = keyList.stream().map(commentMap::get).toList();
        List<List<CommentVO>> filteredList = filterList(reformedList, boardMid);

        log.info("댓글 목록 : ");
        log.info(filteredList);
        return filteredList;
    }

    // 권한 체크하여 댓글 필터링
    private List<List<CommentVO>> filterList(List<List<CommentVO>> comments, Long boardMid) {
        MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 어드민은 모든 댓글을 볼 수 있음.
        if (currentMember.hasAuthorities("ADMIN"))
            return comments;

        // 비밀댓글은 게시글 작성자와 댓글 작성자만 볼 수 있음.
        return comments.stream().filter(commentVO -> {
            if (commentVO.get(0).isSecret()) {
                return currentMember.getMid() == boardMid || currentMember.getMid() == commentVO.get(0).getMid();
            }
            return true;
        }).toList();
    }

    private void addWriterNameToList(List<CommentVO> commemtList) {
        Map<Long, String> writerMap = new HashMap<>();
        for (CommentVO comment : commemtList) {
            // Mid 정보로 db에서 댓글 작성자 이름 가져오기
            String writerName = writerMap.get(comment.getMid());
            if (writerName == null) {
                Optional<Member> optionalMember = memberRepository.findById(comment.getMid());
                if (optionalMember.isPresent()) {
                    Member member = optionalMember.get();
                    writerName = member.getName();
                    writerMap.put(member.getMid(), writerName);
                }
            }
            comment.setCommentWriterName(writerName);

            // targetMid로 db에서 답글 대상 이름 가져오기
            Long targetMid = comment.getTargetMid();
            if (targetMid != null) {
                String targetUserName = writerMap.get(targetMid);
                if (targetUserName == null) {
                    Optional<Member> optionalTargetMember = memberRepository.findById(targetMid);
                    if (optionalTargetMember.isPresent()) {
                        Member targetMember = optionalTargetMember.get();
                        targetUserName = targetMember.getName();
                    }
                }
                comment.setTargetUserName(targetUserName);
            }
        }
    }

    @Override
    public Long insert(CommentVO commentVO, Long boardMid) {
        log.info("댓글등록 서비스 시작");

        if (!StringUtils.hasText(commentVO.getCommentContent())) {
            throw new RuntimeException("댓글의 내용이 비어 있습니다.");
        }

        // mid 변조를 방지하기 위해 재설정
        MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        commentVO.setMid(currentMember.getMid());

        /*
        답글일 경우

        1. 대상 댓글이 존재해야 함. (답글이면 안됨)
        2. 댓글과 답글이 같은 게시글에 있어야 함
        3. 답글 대상 작성자가 댓글, 답글 작성에 참여했어야 함.
        4. 대상 댓글이 비밀댓글이면
            a. 어드민 또는 글작성자 또는 댓글작성자여야 함.
            b. 답글도 비밀댓글
        */
        if (commentVO.getTargetCno() != null) {
            CommentVO parentComment = commentDAO.get(commentVO.getTargetCno());
            //1, 2번 체크
            if (parentComment == null || parentComment.getTargetCno() != null || parentComment.getBno() != commentVO.getBno())
                return 0L;

            // 3번 위반 체크
            List<CommentVO> replies = commentDAO.getReplies(commentVO.getTargetCno());
            replies.add(parentComment);
            Set<Long> commentsWriters = replies.stream().map(comNReplies -> comNReplies.getMid()).collect(Collectors.toSet());
            if (!commentsWriters.contains(commentVO.getTargetMid()))
                return 0L;

            // 4번
            if (parentComment.isSecret()) {
                // 4-a
                if (!currentMember.hasAuthorities("ADMIN") &&
                        currentMember.getMid() != boardMid && currentMember.getMid() != parentComment.getMid()) {
                    throw new AccessDeniedException("403");
                }
                //4-b
                commentVO.setSecret(true);
            }
        }
        return commentDAO.insert(commentVO);
    }

    @Override
    public int delete(Long cno) {
        log.info("댓글 삭제 서비스 시작");
        MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommentVO commentVO = commentDAO.get(cno);

        if (currentMember.hasAuthorities("ADMIN") || currentMember.getMid() == commentVO.getMid()) {
            // 답글이 하나로도 달린 댓글이면 cno, bno, mid는 남기고 commentContent만 삭제. 또한 del을 true로 설정
            // 그 외의 경우 그냥 삭제
            if (commentVO.getTargetCno() == null) {
                // 댓글인 경우
                // 답글이 하나 이상 있으면 commentContent만 삭제하고 del을 true로 설정
                // 답글이 하나도 없으면 완전히 삭제
                if (commentDAO.getReplies(commentVO.getCno()).size() >= 1) {
                    commentVO.setCommentContent("");
                    commentVO.setDel(true);
                    return commentDAO.update(commentVO);
                } else
                    return commentDAO.delete(cno);
            } else {
                // 답글인 경우 완전히 삭제
                // 또한 댓글이 삭제되어 있고 그 댓글에 달린 답글이 1개뿐이면 댓글도 삭제
                CommentVO comment = commentDAO.get(commentVO.getTargetCno());
                if (comment.isDel()) {
                    List<CommentVO> replies = commentDAO.getReplies(commentVO.getTargetCno());
                    if (replies.size() == 1) {
                        commentDAO.delete(comment.getCno());
                    }
                }
                return commentDAO.delete(cno);
            }
        } else
            throw new AccessDeniedException("403");
    }

    @Override
    public int update(long cno, String commentContent) {
        log.info("댓글 수정 서비스 시작");

        if (!StringUtils.hasText(commentContent)) {
            throw new RuntimeException("댓글의 내용이 비어 있습니다.");
        }

        MemberSecurityDTO currentMember = (MemberSecurityDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CommentVO commentVO = new CommentVO();
        commentVO.setMid(currentMember.getMid());
        commentVO.setCno(cno);
        commentVO.setCommentContent(commentContent);
        commentVO.setDel(false);

        CommentVO commentVODB = commentDAO.get(commentVO.getCno());

        if (commentVODB.isDel()) return 0;

        if (commentVO.getMid() == commentVODB.getMid())
            return commentDAO.update(commentVO);
        else
            throw new AccessDeniedException("403");
    }
}
