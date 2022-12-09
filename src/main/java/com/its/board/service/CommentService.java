package com.its.board.service;

import com.its.board.dto.CommentDTO;
import com.its.board.entity.BoardEntity;
import com.its.board.entity.CommentEntity;
import com.its.board.repository.BoardRepository;
import com.its.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    // boardRepository 로 부터 DB 를 받아와야 한다.
    private final BoardRepository boardRepository;

    // 댓글 작성
    public Long save(CommentDTO commentDTO) {
        BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardId()).get();
        CommentEntity commentEntity = CommentEntity.toSaveEntity(boardEntity, commentDTO);
        Long id = commentRepository.save(commentEntity).getId();
        return id;
    }

    // 댓글 목록
    @Transactional
    public List<CommentDTO> findAll(Long boardId) {
        // select * from comment_table where board_id = ?
        BoardEntity boardEntity = boardRepository.findById(boardId).get();
        // 1. comment_table 에서 직접 해당 게시글의 댓글 목록을 가져오기
        List<CommentEntity> commentEntityList =
                commentRepository.findAllByBoardEntityOrderByIdDesc(boardEntity);
        // 2. BoardEntity 를 조회해서 댓글 목록 가져오기
//        List<CommentEntity> commentEntityList1 = boardEntity.getCommentEntityList();
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (CommentEntity commentEntity : commentEntityList) {
            commentDTOList.add(CommentDTO.toCommentDTO(commentEntity));
        }
        return commentDTOList;
    }
}
