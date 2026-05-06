package MasterTable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Hotel {
    @Entity
//@Table(name = "person")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Person {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "Category", nullable = false, length = 100)
        private Category category;

        @Column(name = "Name", nullable = false, length = 100)
        private String name;

        @Column(name = "owner", length = 150)
        private String owner;

        @Column(name = "contact", length = 150)
        private String contact;

        @Column(name = "Adress", nullable = false, length = 100)
        private String adress;

        @Column(name = "city", length = 150)
        private String city;

        @Column(name = "citycode")
        private Integer citycode;

        @Column(name = "Phone", unique = true,  length = 150)
        private String phone;

        @Column(name = "Nr of Rooms",  length = 150)
        private Integer nrRooms;

        @Column(name = "Nr of Beds",  length = 150)
        private Integer nrBeds;

        @Column(name = "Last reported Data",  length = 150)
        private Integer lastReportedData;












    }

}
