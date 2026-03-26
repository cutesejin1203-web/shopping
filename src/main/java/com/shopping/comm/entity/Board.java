package com.shopping.comm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title;      // 제목

    @Column(columnDefinition = "TEXT")
    private String content;    // 내용

    private String writer;     // 작성자 이름 (또는 이메일)

    // 🚀 [핵심] 비밀글 관련 컬럼
    private boolean isSecret;  // 비밀글 여부 (true면 비밀글)
    private String password;   // 게시글 비밀번호 (비밀글일 때만 값 있음)

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime regTime; // 작성 시간

    @Column(columnDefinition = "TEXT")
    private String answer;

    private LocalDateTime answerRegTime;

    // 답변 등록/수정용 편의 메소드
    public void updateAnswer(String answer) {
        this.answer = answer;
        this.answerRegTime = LocalDateTime.now();
    }
}
