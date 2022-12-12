package com.its.board.repository;

import com.its.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> { // interface 는 추상메서드를 써야한다.
    // update board_table set board_hits=board_hits+1 where id=?
    @Modifying
//    -native query- : DB와 같이 사용하고 싶을 때 ★★★
//    @Query(value = "update board_table b set board_hits=b.board_hits + 1 where b.id = :id", nativeQuery = true)
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits + 1 where b.id = :id")
    // 아래 @Param 의 id 와 @Query 의 :id 값과 동일해야 한다.
    // 만약 @Query 의 :id123 값이면 @Param("id123") 이어야 한다.
    void updateHits(@Param("id") Long id);

    // 검색 처리

    // select * from board_table where board_writer like '%q%';
    List<BoardEntity> findByBoardWriterContaining(String q);

    // select * from board_table where board_writer like '%q%' order by id desc;
    List<BoardEntity> findByBoardWriterContainingOrderByIdDesc(String q);

    // select * from board_table where board_title like '%q%' order by id desc;
    List<BoardEntity> findByBoardTitleContainingOrderByIdDesc(String q);

    // select * from board_table where board_title like '%q%' or board_writer like '%q%' order by id desc;
    List<BoardEntity> findByBoardTitleContainingOrBoardWriterContainingOrderByIdDesc(String title, String writer);
//    List<BoardEntity> findBy + BoardTitleContaining + Or + BoardWriterContaining + OrderBy + Id + Desc(String title, String writer);
}
