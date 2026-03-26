package com.shopping.board.service;

import com.shopping.board.vo.BoardVO;
import java.util.List;

public interface BoardService {

    // 1. 게시글 등록
    void saveBoard(BoardVO vo);

    // 2. 게시글 목록 쫙 가져오기
    List<BoardVO> getBoardList();

    // 3. 게시글 상세 보기
    BoardVO getBoardDetail(Long id);

    // 🚀 4. [핵심] 비밀글 비밀번호가 맞는지 검사하기
    boolean verifyPassword(Long id, String inputPassword);

    // 🚀 [추가] 답변 등록 및 수정 기능
    void updateAnswer(Long id, String answer);

    // 🚀 [추가] 게시글 삭제 (관리자용)
    void deleteBoard(Long id);
}