package org.company.trashambulance.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "forward_data")
public class ForwardData {
    @Id
    @UuidGenerator
    private String id;
    private Long messageId;
    private Long chatId;
    @CreationTimestamp
    private LocalDateTime creationDate;
    @OneToOne
    @JoinColumn(name = "form_id", referencedColumnName = "id", nullable = true)
    private Form form;
}
