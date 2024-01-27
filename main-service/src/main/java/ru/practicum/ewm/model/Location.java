package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;

@Entity(name = "location")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "id")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Float lat;
    @Column
    private Float lon;
}