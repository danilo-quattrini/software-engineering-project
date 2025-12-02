package it.unicam.cs.ids2425.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.payment.referable.Referable;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.roles.seller.Seller;
import it.unicam.cs.ids2425.users.roles.Buyer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "events")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Event extends Referable {


    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1024)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Embedded
    private Location location;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state = EventState.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entertainer_id", nullable = false)
    @JsonIgnore
    private User entertainer;

    @ManyToMany
    @JoinTable(
            name = "event_buyers",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "buyer_id")
    )
    private Set<Buyer> buyers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "event_invited_sellers",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "seller_id")
    )
    private Set<Seller> invitedSellers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "event_confirmed_sellers",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "seller_id")
    )
    private Set<Seller> confirmedSellers = new HashSet<>();

    public void addBuyer(Buyer buyer) {
        buyers.add(buyer);
    }

    public boolean hasBuyer(Buyer buyer) {
        return buyers.contains(buyer);
    }

    public void inviteSeller(Seller seller) {
        invitedSellers.add(seller);
    }

    public void confirmSeller(Seller seller) {
        invitedSellers.remove(seller);
        confirmedSellers.add(seller);
    }

    public boolean isSellerConfirmed(Seller seller) {
        return confirmedSellers.contains(seller);
    }

    public boolean isSellerInvited(Seller seller) {
        return invitedSellers.contains(seller);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}