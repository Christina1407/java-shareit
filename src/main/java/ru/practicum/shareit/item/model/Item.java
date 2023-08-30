package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Entity
@Table(name = "items", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Item {
    @Id
    @Column(name = "item_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;
}
