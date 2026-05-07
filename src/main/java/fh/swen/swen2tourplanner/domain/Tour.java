package fh.swen.swen2tourplanner.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fh.swen.swen2tourplanner.domain.enums.TransportType;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tours")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String fromLocation;

    @Column(nullable = false)
    private String toLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType transportType;

    @Column
    private double distanceKm;

    @Column
    private Integer estimatedTime;

    @Column
    private String route;

    @OneToMany(mappedBy = "tour")
    @JsonIgnore
    private List<TourLog> tourLogs;

}
