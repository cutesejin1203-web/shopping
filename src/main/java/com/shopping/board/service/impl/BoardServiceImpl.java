package com.shopping.board.service.impl;

import com.shopping.board.repository.BoardRepository;
import com.shopping.board.service.BoardService;
import com.shopping.board.vo.BoardVO;
import com.shopping.comm.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    // 1. 게시글 등록
    @Override
    public void saveBoard(BoardVO vo) {
        Board board = new Board();
        board.setTitle(vo.getTitle());
        board.setContent(vo.getContent());
        board.setWriter(vo.getWriter());
        board.setSecret(vo.isSecret());
        board.setPassword(vo.getPassword()); // 비밀글일 때만 4자리 숫자 같은 게 들어옴

        boardRepository.save(board);
    }

    // 2. 게시글 목록 쫙 가져오기
    @Override
    @Transactional(readOnly = true)
    public List<BoardVO> getBoardList() {
        return boardRepository.findAll().stream().map(board -> {
            BoardVO vo = new BoardVO();
            vo.setId(board.getId());
            vo.setTitle(board.getTitle());
            vo.setWriter(board.getWriter());
            vo.setSecret(board.isSecret());
            vo.setRegTime(board.getRegTime());
            // 💡 목록에서는 내용(content)과 비밀번호(password)는 안 보내는 게 보안상 좋아!
            vo.setAnswer(board.getAnswer());
            vo.setAnswerRegTime(board.getAnswerRegTime());

            return vo;
        }).collect(Collectors.toList());
    }

    // 3. 게시글 상세 보기 (비밀글이든 아니든 아이디로 꺼내오기)
    @Override
    @Transactional(readOnly = true)
    public BoardVO getBoardDetail(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        BoardVO vo = new BoardVO();
        vo.setId(board.getId());
        vo.setTitle(board.getTitle());
        vo.setContent(board.getContent());
        vo.setWriter(board.getWriter());
        vo.setSecret(board.isSecret());
        vo.setRegTime(board.getRegTime());

        vo.setAnswer(board.getAnswer());
        vo.setAnswerRegTime(board.getAnswerRegTime());

        return vo;
    }

    // 🚀 4. [핵심] 비밀글 비밀번호 검증기!
    @Override
    @Transactional(readOnly = true)
    public boolean verifyPassword(Long id, String inputPassword) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        // DB에 저장된 비번과 프론트에서 입력한 비번이 똑같은지 확인!
        return board.getPassword() != null && board.getPassword().equals(inputPassword);
    }

    @Override
    @Transactional
    public void updateAnswer(Long id, String answer) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        // 엔티티 필드에 값만 넣어주면 트랜잭션 종료 시 자동 UPDATE!
        board.setAnswer(answer);
        board.setAnswerRegTime(java.time.LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        boardRepository.delete(board);
    }

}