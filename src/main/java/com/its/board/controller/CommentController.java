package com.its.board.controller;

import com.its.board.dto.CommentDTO;
import com.its.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성, 댓글 목록
    // axios 로 넘어오니깐 @RequestBody
    @PostMapping("/save")
    public ResponseEntity save(@RequestBody CommentDTO commentDTO) {
        commentService.save(commentDTO);
        // 목록을 가져와서
        List<CommentDTO> commentDTOList = commentService.findAll(commentDTO.getBoardId());
        // 목록을 뿌려준다
        return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
    }
}
