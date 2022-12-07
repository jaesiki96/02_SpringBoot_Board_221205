package com.its.board;

import com.its.board.dto.BoardDTO;
import com.its.board.entity.BoardEntity;
import com.its.board.repository.BoardRepository;
import com.its.board.service.BoardService;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.stream.IntStream;

@SpringBootTest
public class BoardTest {
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    private BoardDTO newBoard(int i) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setBoardWriter("writer" + i);
        boardDTO.setBoardTitle("title" + i);
        boardDTO.setBoardPass("pass" + i);
        boardDTO.setBoardContents("contents" + i);
        return boardDTO;
    }

    @Test
    @Transactional
    @Rollback(value = true)
    @DisplayName("글작성 테스트")
    public void boardSaveTest() throws IOException {
        BoardDTO boardDTO = newBoard(1);
        Long savedId = boardService.save(boardDTO);
        BoardDTO findBoard = boardService.findById(savedId);
        assertThat(boardDTO.getBoardWriter()).isEqualTo(findBoard.getBoardWriter());
    }

    @Test
    @Transactional
    @Rollback(value = false)
    @DisplayName("글작성 여러개")
    public void saveList() throws IOException {
        for (int i = 1; i <= 20; i++) {
            boardService.save(newBoard(i));
        }
        // 위 for 문 과 아래 IntStream 문 은 같은 표현 (편한 방식 사용하면 된다),
        // rangeClosed(21, 40) 21~40 의 숫자를 만들고 forEach 로 21~40 까지 하나씩 i 변수로 접근
//        IntStream.rangeClosed(21, 40).forEach(i -> {
//            boardService.save(newBoard(i));
//        });
    }

    @Test
    @Transactional
    @DisplayName("연관관계 조회 테스트")
    public void findTest() {
        // 파일이 첨부된 게시글 조회
        BoardEntity boardEntity = boardRepository.findById(70L).get();
        // 첨부파일의 originalFileName 조회
        System.out.println("boardEntity.getBoardFileEntityList() = " + boardEntity.getBoardFileEntityList().get(0).getOriginalFileName());
        // native query
    }
}
