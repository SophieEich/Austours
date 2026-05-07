package MasterTable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "hotels")
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

    }


