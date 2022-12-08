package com.its.board.service;

import com.its.board.dto.BoardDTO;
import com.its.board.entity.BoardEntity;
import com.its.board.entity.BoardFileEntity;
import com.its.board.repository.BoardFileRepository;
import com.its.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    // 글 작성
    public Long save(BoardDTO boardDTO) throws IOException {
//        if (boardDTO.getBoardFile().isEmpty()) {
        if (boardDTO.getBoardFile().size() == 0) {
            System.out.println("파일 없음");
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            return boardRepository.save(boardEntity).getId();
        } else {
            System.out.println("파일 있음");
            // 게시글 정보를 먼저 저장하고 해당 게시글의 entity 를 가져옴
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long savedId = boardRepository.save(boardEntity).getId();
            BoardEntity entity = boardRepository.findById(savedId).get();
            // 파일이 담긴 list 를 반복문으로 접근하여 하나씩 이름 가져오고, 저장용 이름 만들고
            // 로컬 경로에 저장하고 board_file_table 에 저장
            for (MultipartFile boardFile : boardDTO.getBoardFile()) {
//                MultipartFile boardFile = boardDTO.getBoardFile(); 중복되는 코드
                String originalFileName = boardFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
                String savePath = "D:\\springboot_img\\" + storedFileName;
                boardFile.transferTo(new File(savePath));
                BoardFileEntity boardFileEntity =
                        BoardFileEntity.toSaveBoardFileEntity(entity, originalFileName, storedFileName);
                boardFileRepository.save(boardFileEntity);
            }
            return savedId;
        }
    }

    // 글 목록
    @Transactional // 부모 Entity 에서 자식 Entity 를 직접 가져올 때 필요
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toDTO(boardEntity));
        }
        return boardDTOList;
    }

    // 상세조회
    @Transactional // 부모 Entity 에서 자식 Entity 를 직접 가져올 때 필요
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) {
            return BoardDTO.toDTO(optionalBoardEntity.get());
        } else {
            return null;
        }
    }

    // 조회수
    @Transactional // 부모 Entity 에서 자식 Entity 를 직접 가져올 때 필요
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

    // 글 삭제
    public void delete(Long id) {
        boardRepository.deleteById(id);
    }
}
