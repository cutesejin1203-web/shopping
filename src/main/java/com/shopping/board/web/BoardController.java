package com.shopping.board.web;

import com.shopping.board.service.BoardService;
import com.shopping.board.vo.BoardVO;
import com.shopping.comm.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 1. 목록 가져오기 (GET)
    @GetMapping
    public ResponseEntity<List<BoardVO>> getList() {
        return ResponseEntity.ok(boardService.getBoardList());
    }

    // 2. 글쓰기 (POST)
    @PostMapping
    public ResponseEntity<String> write(@RequestBody BoardVO boardVO) {
        boardService.saveBoard(boardVO);
        return ResponseEntity.ok("success");
    }

    // 3. 상세 보기 (GET)
    @GetMapping("/{id}")
    public ResponseEntity<BoardVO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoardDetail(id));
    }

    // 🚀 4. [핵심] 비밀글 비밀번호 확인 (POST)
    @PostMapping("/{id}/verify")
    public ResponseEntity<Boolean> verifySecret(@PathVariable Long id, @RequestBody BoardVO boardVO) {
        // 리액트에서 모달창에 입력한 비번을 담아서 보내면 여기서 검사!
        boolean isMatch = boardService.verifyPassword(id, boardVO.getPassword());

        if (isMatch) {
            return ResponseEntity.ok(true); // 비번 맞음! 문 열어!
        } else {
            return ResponseEntity.ok(false); // 비번 틀림! 돌아가!
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.ok("deleted");
    }

    @PostMapping("/{id}/answer")
    @PreAuthorize("hasRole('ADMIN')") // 💡 관리자만 호출 가능하게!
    public ResponseEntity<String> updateAnswer(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String answer = request.get("answer");
        boardService.updateAnswer(id, answer);
        return ResponseEntity.ok("success");
    }
}