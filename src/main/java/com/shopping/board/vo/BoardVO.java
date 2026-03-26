package com.shopping.board.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BoardVO {
    private Long id;
    private String title;
    private String content;
    private String writer;
    // 🚀 비밀글 관련
    private boolean isSecret;
    private String password; // 💡 글 쓸 때나 비번 확인할 때만 프론트에서 넘어옴
    private LocalDateTime regTime;
    private String answer;
    private LocalDateTime answerRegTime;
}