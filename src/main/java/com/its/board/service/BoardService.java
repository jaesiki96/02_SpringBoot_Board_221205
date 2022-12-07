package com.its.board.service;

import com.its.board.dto.BoardDTO;
import com.its.board.entity.BoardEntity;
import com.its.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    // 글 작성
    public Long save(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
        return boardRepository.save(boardEntity).getId();
    }

    // 글 목록
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toDTO(boardEntity));
        }
        return boardDTOList;
    }

    // 상세조회
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) {
            return BoardDTO.toDTO(optionalBoardEntity.get());
        } else {
            return null;
        }
    }

    // 조회수
    @Transactional
    // Java → JPA → DB
    // JPA 는 중간 역할
    // JPA: JPA 가 가지고 있는 DB 가 남아있다. (JPA 캐시) -> 그래서 동기화가 끝난 후 @Transactional 을 통해 작업 진행..?
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    // 글 수정
    // 호출 - 가져오기 (중요) ★★★
    // save (id 가 있기 때문에 update query 로 전환)
    public void update(BoardDTO boardDTO) {
        BoardEntity boardEntity = BoardEntity.toUpdateEntity(boardDTO);
        boardRepository.save(boardEntity);
    }
}
