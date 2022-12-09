package com.its.board.entity;

import com.its.board.dto.CommentDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
public class CommentEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String commentWriter;

    @Column(length = 200, nullable = false)
    private String commentContents;

    // board_id 참조 (board = 부모 , comment = 자식)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    // 부모 entity 를 받는다.
    private BoardEntity boardEntity;

    public static CommentEntity toSaveEntity(BoardEntity entity, CommentDTO commentDTO) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCommentWriter(commentDTO.getCommentWriter());
        commentEntity.setCommentContents(commentDTO.getCommentContents());
        commentEntity.setBoardEntity(entity);
        return commentEntity;
    }
}
