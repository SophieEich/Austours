package MasterTable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "occupancy")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Occupancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    // WICHTIG: Hier muss exakt der Name aus deinem Screenshot stehen
    @JoinColumn(name = "hotel id", nullable = false)
    private Hotel hotel;

    @Column(name = "occ year")
    private int year;

    @Column(name = "occ month")
    private int month;

    @Column(name = "room occupancy")
    private int roomOccupancy;

    @Column(name = "bed occupancy")
    private int bedOccupancy;


}
