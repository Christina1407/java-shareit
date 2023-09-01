package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;


import javax.persistence.*;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email", nullable = false, length = 320)
    private String email;
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    @OneToMany(mappedBy= "owner")
    List<Item> items;
}
