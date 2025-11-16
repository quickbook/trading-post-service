package com.tps.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.tps.enums.InstrumentType;
import com.tps.enums.LeverageProfile;

@Entity
@Table(name = "firm_leverages", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"firm_id", "profile", "instrument"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FirmLeverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_id", nullable = false)
    private FirmCard firmCard;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile", length = 30, nullable = false)
    private LeverageProfile profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "instrument", length = 30, nullable = false)
    private InstrumentType instrument;

    /**
     * Numeric leverage factor: 100 -> 1:100, 2 -> 1:2
     */
    @Column(name = "leverage_factor", nullable = false)
    private Integer leverageFactor;
}
