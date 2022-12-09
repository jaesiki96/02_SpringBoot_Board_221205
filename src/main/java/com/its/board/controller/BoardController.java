package com.its.board.controller;

import com.its.board.dto.BoardDTO;
import com.its.board.dto.CommentDTO;
import com.its.board.service.BoardService;
import com.its.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    // 글 작성 페이지
    @GetMapping("/save")
    public String saveForm() {
        return "boardPages/boardSave";
    }

    // 글 작성
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        boardService.save(boardDTO);
        return "redirect:/board/";
    }

    // 글 목록
    @GetMapping("/")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        return "boardPages/boardList";
    }

    // 페이징 목록 ex) /board?page=1
    // Pageable => springframework.data.domain.Pageable
    // @PageableDefault(page = 1) Pageable 이 없으면 기본 페이지를 1페이지로 한다.
    @GetMapping
    public String paging(@PageableDefault(page = 1) Pageable pageable, Model model) {
        System.out.println("page" + pageable.getPageNumber());
        Page<BoardDTO> boardDTOList = boardService.paging(pageable);
        model.addAttribute("boardList", boardDTOList);
        // blockLimit --> 페이징 버튼에 보여지는 페이지 갯수
        int blockLimit = 3;
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < boardDTOList.getTotalPages()) ? startPage + blockLimit - 1 : boardDTOList.getTotalPages();
        // 삼항연산자
//        int test = 10;
//        int num = (test > 5) ? test: 100;
//        if (test > 5) {
//            num = test;
//        } else {
//            num = 100;
//        }

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "boardPages/paging";
    }


    // 글 상세조회
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) { // id 값 @PathVariable 데이터를 받아와야 하니깐 Model
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        if (commentDTOList.size() > 0) {
            model.addAttribute("commentList", commentDTOList);
        } else {
            model.addAttribute("commentList", "empty");
        }
        model.addAttribute("board", boardDTO);
        return "boardPages/boardDetail";
    }

    // 글 수정 페이지
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "boardPages/boardUpdate";
    }

    // 글 수정
    @PostMapping("/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        boardService.update(boardDTO);
        BoardDTO boardDTO1 = boardService.findById(boardDTO.getId());
        model.addAttribute("board", boardDTO1);
        return "boardPages/boardDetail";
    }

    // 글 수정(axios)
    // update: 매개변수 타입이 다르기 때문에 같은 이름으로 매서드를 만들 수 있음 / Overroading
    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody BoardDTO boardDTO) {
        boardService.update(boardDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 글 삭제
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/";
    }

    // 글 삭제(axios)
    // 위 GetMapping delete 와 매개변수(@PathVariable Long id) 가 같아서 이름을 deleteByAxios 처럼 다르게 해야한다
    @DeleteMapping("/{id}")
    public ResponseEntity deleteByAxios(@PathVariable Long id) {
        boardService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
