package com.its.board.controller;

import com.its.board.dto.BoardDTO;
import com.its.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    // 글 작성 페이지
    @GetMapping("/save")
    public String saveForm() {
        return "boardPages/boardSave";
    }

    // 글 작성
    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO) {
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

    // 글 상세조회
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) { // id 값 @PathVariable 데이터를 받아와야 하니깐 Model
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
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
}
