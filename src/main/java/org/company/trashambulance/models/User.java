package org.company.trashambulance.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @UuidGenerator
    private String id;
    private Long telegramId;
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "phone_id", referencedColumnName = "id")
    private Phone phone;
    @OneToMany(mappedBy="user", fetch = FetchType.EAGER)
    private Set<Form> forms;
    @CreationTimestamp
    private LocalDateTime firstLoginDate;
    private Boolean isActive;
    private String state;
    private long chatId;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.isActive = true;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, telegramId, name, firstLoginDate, isActive, state);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id) &&
                Objects.equals(telegramId, user.telegramId) &&
                Objects.equals(name, user.name) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(firstLoginDate, user.firstLoginDate) &&
                Objects.equals(isActive, user.isActive) &&
                Objects.equals(state, user.state);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", telegramId=" + telegramId +
                ", name='" + name + '\'' +
                ", firstLoginDate=" + firstLoginDate +
                ", isActive=" + isActive +
                ", state='" + state + '\'' +
                '}';
    }
}
