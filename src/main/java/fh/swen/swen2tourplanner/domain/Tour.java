package fh.swen.swen2tourplanner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fh.swen.swen2tourplanner.domain.enums.TransportType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.List;

@Entity
@Table(name = "tours")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Indexed
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FullTextField
    @Column(nullable = false)
    private String name;

    @FullTextField
    @Column(nullable = false)
    private String description;

    @FullTextField
    @Column(nullable = false)
    private String fromLocation;

    @FullTextField
    @Column(nullable = false)
    private String toLocation;

    @KeywordField
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType transportType;

    @Column
    private double distanceKm;

    @Column
    private Integer estimatedTime;

    @Column
    private String route;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TourLog> tourLogs;
}
