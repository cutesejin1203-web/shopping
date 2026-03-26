package com.shopping.board.repository;

import com.shopping.comm.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 💡 최신순으로 정렬해서 가져오고 싶다면?
    List<Board> findAllByOrderByRegTimeDesc();
}