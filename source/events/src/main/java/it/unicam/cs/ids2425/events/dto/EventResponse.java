package it.unicam.cs.ids2425.events.dto;

import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.events.Event;
import it.unicam.cs.ids2425.events.EventState;
import it.unicam.cs.ids2425.events.EventType;
import it.unicam.cs.ids2425.users.User;

import java.util.Collections;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record EventResponse(
        Long id,
        String title,
        String description,
        EventType type,
        Location location,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        BigDecimal price,
        String currencyCode,
        EventState state,
        Long entertainerId,
        String entertainerEmail,
        Set<Long> buyerIds,
        Set<String> buyerEmails,
        Set<Long> invitedSellerIds,
        Set<String> invitedSellerEmails,
        Set<Long> confirmedSellerIds,
        Set<String> confirmedSellerEmails
) {

    public static EventResponse from(Event event) {
        BigDecimal price = null;
        if (event.getAmount() != null) {
            price = BigDecimal.valueOf(event.getAmount());
        }

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getType(),
                event.getLocation(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                price,
                event.getCurrencyCode(),
                event.getState(),
                event.getEntertainer() != null ? event.getEntertainer().getId() : null,
                event.getEntertainer() != null ? event.getEntertainer().getEmail() : null,
                toIds(event.getBuyers()),
                toEmails(event.getBuyers()),
                toIds(event.getInvitedSellers()),
                toEmails(event.getInvitedSellers()),
                toIds(event.getConfirmedSellers()),
                toEmails(event.getConfirmedSellers())
        );
    }

    private static Set<Long> toIds(Set<? extends User> users) {
        return users == null
                ? Collections.emptySet()
                : users.stream().map(User::getId).collect(Collectors.toSet());
    }

    private static Set<String> toEmails(Set<? extends User> users) {
        return users == null
                ? Collections.emptySet()
                : users.stream().map(User::getEmail).collect(Collectors.toSet());
    }
}