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
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "chatHistory", cascade = CascadeType.REMOVE,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();


}
