package com.booking.football_pitch.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "pitches",
        indexes = @Index(name = "idx_pitches_venue_status", columnList = "venue_id, status"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "venue", "bookings", "images", "schedules", "unavailabilities", "amenities" })
public class Pitch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "pitch_type", nullable = false, length = 10)
    private PitchType pitchType;

    @Column(length = 30)
    private String surface;

    @Column(name = "hourly_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal hourlyPrice;

    @Default
    @Column(name = "slot_minutes", nullable = false)
    private Integer slotMinutes = 60;

    @Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ĐÃ CẬP NHẬT CASCADE TẠI ĐÂY ĐỂ HỖ TRỢ XÓA SÂN KHÔNG BỊ VƯỚNG KHÓA NGOẠI
    @Default
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @Default
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PitchImage> images = new ArrayList<>();

    @Default
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PitchSchedule> schedules = new ArrayList<>();

    @Default
    @OneToMany(mappedBy = "pitch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PitchUnavailability> unavailabilities = new ArrayList<>();

    @Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pitch_amenities",
            joinColumns = @JoinColumn(name = "pitch_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private Set<Amenity> amenities = new HashSet<>();

    public enum PitchType {
        FIVE,
        SEVEN,
        ELEVEN
    }

    public enum Status {
        ACTIVE,
        MAINTENANCE,
        INACTIVE
    }
}