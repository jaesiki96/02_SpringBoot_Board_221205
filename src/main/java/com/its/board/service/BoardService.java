package com.its.board.service;

import com.its.board.dto.BoardDTO;
import com.its.board.entity.BoardEntity;
import com.its.board.entity.BoardFileEntity;
import com.its.board.entity.MemberEntity;
import com.its.board.repository.BoardFileRepository;
import com.its.board.repository.BoardRepository;
import com.its.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final MemberRepository memberRepository;

    // 글 작성
    public Long save(BoardDTO boardDTO) throws IOException {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(boardDTO.getBoardWriter()).get();
//        if (boardDTO.getBoardFile().isEmpty()) {
        if (boardDTO.getBoardFile() == null || boardDTO.getBoardFile().size() == 0) {
            System.out.println("파일 없음");
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO, memberEntity);
            return boardRepository.save(boardEntity).getId();
        } else {
            System.out.println("파일 있음");
            // 게시글 정보를 먼저 저장하고 해당 게시글의 entity 를 가져옴
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO, memberEntity);
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
    // Sort.by(Sort.Direction.DESC,"id" => Entity 에 정의한 id 컬럼을 기준으로 내림차순!
    @Transactional // 부모 Entity 에서 자식 Entity 를 직접 가져올 때 필요
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<BoardDTO> boardDTOList = new ArrayList<>();
        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toDTO(boardEntity));
        }
        return boardDTOList;
    }

    // 페이징 목록
    // page => 1부터 시작 안하고 0부터 시작하기 때문에 1을 뺀다.
    // limit => 한 페이지에 몇 개씩
    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1;
        // pageLimit --> 한 페이지에 보여줄 게시글 갯수
        final int pageLimit = 5;
        Page<BoardEntity> boardEntities = boardRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));
        // Page<BoardEntity> -> Page<BoardDTO> // map --> 옮겨 담아주는 역할
        Page<BoardDTO> boardList = boardEntities.map(
                // boardEntities  에 담긴 boardEntity 객체를 board 에 담아서
                // boardDTO 객체로 하나씩 옮겨 담는 과정
                board -> new BoardDTO(board.getId(),
                        board.getBoardWriter(),
                        board.getBoardTitle(),
                        board.getCreatedTime(),
                        board.getBoardHits()
                )
        );
        return boardList;
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

    // 검색 처리
    public List<BoardDTO> search(String type, String q) {
        // 작성자 검색
        // select * from board_table where board_writer like '%q%';
        List<BoardDTO> boardDTOList = new ArrayList<>();
        List<BoardEntity> boardEntityList = null;
        if (type.equals("boardWriter")) {
            boardEntityList = boardRepository.findByBoardWriterContainingOrderByIdDesc(q);
        } else if (type.equals("boardTitle")) {
            boardEntityList = boardRepository.findByBoardTitleContainingOrderByIdDesc(q);
        } else {
            boardEntityList = boardRepository.findByBoardTitleContainingOrBoardWriterContainingOrderByIdDesc(q, q);
        }

        for (BoardEntity boardEntity : boardEntityList) {
            boardDTOList.add(BoardDTO.toDTO(boardEntity));
        }
        return boardDTOList;
    }
}
