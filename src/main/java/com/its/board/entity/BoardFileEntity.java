package com.its.board.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "board_file_table")
public class BoardFileEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalFileName;

    @Column
    private String storedFileName;

    // 자식 Entity 에서는 자기를 기준으로 부모 Entity 와 어떤 관계인지
    // 게시글 : 첨부파일 = 1 : N
    // 첨부파일 : 게시글 = N : 1
    @ManyToOne(fetch = FetchType.LAZY) // N : 1 (게시글(1)을 기준으로 첨부파일(N)을 여러개 둘 수 있다)
    @JoinColumn(name = "board_id") // 테이블에 생성될 컬럼 이름
    private BoardEntity boardEntity; // 부모 Entity 타입의 필드가 와야함.

    // 자식 Entity 객체를 만들 때 부모 Entity 가 필요하다. ★★
    public static BoardFileEntity toSaveBoardFileEntity(BoardEntity entity, String originalFileName, String storedFileName) {
        BoardFileEntity boardFileEntity = new BoardFileEntity();
        boardFileEntity.setOriginalFileName(originalFileName);
        boardFileEntity.setStoredFileName(storedFileName);
        boardFileEntity.setBoardEntity(entity);
        return boardFileEntity;
    }
}
