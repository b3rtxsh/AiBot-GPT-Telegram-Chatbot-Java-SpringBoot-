package com.telegram.bot.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "chatHistory", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<GptMessage> messages = new ArrayList<>();

//    @Version
//    @Column(nullable = false)
//    private Integer version = 0;  // Used for optimistic locking

//    public ChatHistory(Long chatId) {
//        this.createdAt = LocalDateTime.now();
//    }

    public ChatHistory(Long chatId) {
        this.createdAt = LocalDateTime.now();
        // Initialize version explicitly
    }

//    public ChatHistory() {
//    }
}
