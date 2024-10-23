package org.company.trashambulance.models;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode
@ToString
@Table(name = "forms")
public class Form {
    @Id
    @UuidGenerator
    private String id;
    private String text;
    private String address;
    @Column(nullable = true)
    private String photo;
    private long telegramId;
    private long chatId;
    private long messageId;
    private boolean isActive;
    @CreationTimestamp
    private LocalDateTime creationDate;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=true)
    private User user;

    public String getFormattedCreationDate() {
        if (creationDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return creationDate.format(formatter);
        }
        return null;
    }
}
