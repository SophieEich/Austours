package MasterTable.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import MasterTable.entity.user.UsersHibernate;


@Entity
@Table(name = "Hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
    public class Hotel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "Category", nullable = false, length = 100)
        private String category;

        @Column(name = "Name", nullable = false, length = 100)
        private String name;

        @Column(name = "owner", length = 150)
        private String owner;

        @Column(name = "contact", length = 150)
        private String contact;

        @Column(name = "Address", nullable = false, length = 100)
        private String address;

        @Column(name = "City", length = 150)
        private String city;

        @Column(name = "Citycode", length = 150)
        private String cityCode;

        @Column(name = "Phone",  length = 150)
        private String phone;

        @Column(name = "[Nr of Rooms]")
        private Integer noRooms;

        @Column(name = "[Nr of Beds]")
        private Integer noBeds;

        @Column(name = "[Last reported Data]")
        private String lastReported;

    // US14: New attribute columns — family-friendly, pet-friendly, spa, fitness

        @Column(name = "family_friendly", nullable = false, columnDefinition = "BIT DEFAULT 0")
        @Builder.Default
        private boolean familyFriendly = false;

        @Column(name = "pet_friendly", nullable = false, columnDefinition = "BIT DEFAULT 0")
        @Builder.Default
        private boolean petFriendly = false;

        @Column(name = "spa", nullable = false, columnDefinition = "BIT DEFAULT 0")
        @Builder.Default
        private boolean spa = false;

        @Column(name = "fitness", nullable = false, columnDefinition = "BIT DEFAULT 0")
        @Builder.Default
        private boolean fitness = false;


// US24: Every Hotel has exactly one Representer (User).
    // One Representer can have many Hotels -> ManyToOne (foreign key representative_id).
    // This way the program knows which hotels are "my" hotels.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "representative_id")
    private UsersHibernate representative;


    // to String for Occupancy Filter
    @Override
    public String toString() {
        return id + " - " + name;
    }

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private java.util.List<Occupancy> occupancies; // Wenn wir ein Hotel löschen in HotelTable, soll es überall gelöscht werden

    }


