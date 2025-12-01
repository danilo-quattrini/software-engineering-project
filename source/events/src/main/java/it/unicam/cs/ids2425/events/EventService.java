package it.unicam.cs.ids2425.events;


import it.unicam.cs.ids2425.events.dto.EventRequest;
import it.unicam.cs.ids2425.events.dto.EventResponse;
import it.unicam.cs.ids2425.events.dto.EventStateUpdateRequest;
import it.unicam.cs.ids2425.events.dto.EventUpdateRequest;
import it.unicam.cs.ids2425.events.dto.InviteByEmail;
import it.unicam.cs.ids2425.users.roles.Buyer;
import it.unicam.cs.ids2425.users.roles.seller.Seller;
import it.unicam.cs.ids2425.users.User;
import it.unicam.cs.ids2425.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Service
@Transactional
public class EventService {

    private static final Set<String> SELLER_ROLES = Set.of("PRODUCER", "DISTRIBUTOR", "TRANSFORMER");

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public EventResponse createEvent(String email, EventRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " does not exist"));

        validateChronology(request.startDateTime(), request.endDateTime());

        BigDecimal price = BigDecimal.valueOf(request.amount());
        ensurePositivePrice(price);

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .type(request.type())
                .location(request.location())
                .startDateTime(request.startDateTime())
                .endDateTime(request.endDateTime())
                .amount(price.doubleValue())
                .currencyCode(request.currencyCode())
                .entertainer(user)
                .state(EventState.DRAFT)
                .build();

        Event saved = eventRepository.save(event);
        return EventResponse.from(saved);
    }

    public EventResponse updateEvent(Long entertainerId, Long eventId, EventUpdateRequest request) {
        Event event = getEventForManagement(entertainerId, eventId);

        if (request.title() != null && !request.title().isBlank()) {
            event.setTitle(request.title());
        }
        if (request.description() != null && !request.description().isBlank()) {
            event.setDescription(request.description());
        }
        if (request.type() != null) {
            event.setType(request.type());
        }
        if (request.location() != null) {
            event.setLocation(request.location());
        }

        LocalDateTime start = request.startDateTime() != null ? request.startDateTime() : event.getStartDateTime();
        LocalDateTime end = request.endDateTime() != null ? request.endDateTime() : event.getEndDateTime();
        validateChronology(start, end);
        event.setStartDateTime(start);
        event.setEndDateTime(end);

        if (request.price() != null) {
            ensurePositivePrice(request.price());
            event.setAmount(request.price().doubleValue());   // prezzo = amount
        }

        return EventResponse.from(event);
    }

    public EventResponse updateState(Long entertainerId, Long eventId, EventStateUpdateRequest request) {
        Event event = getEventForManagement(entertainerId, eventId);
        event.setState(request.state());
        return EventResponse.from(event);
    }

    public void deleteEvent(Long entertainerId, Long eventId) {
        Event event = getEventForManagement(entertainerId, eventId);
        event.setEntertainer(null);
        eventRepository.delete(event);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream().map(EventResponse::from).toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public EventResponse getEvent(Long eventId) {
        return EventResponse.from(getEventById(eventId));
    }

    public EventResponse bookEvent(Long eventId, Long buyerId) {
        Event event = getEventById(eventId);
        ensureEventIsActive(event);
        Buyer buyer = getUser(buyerId, Buyer.class);
        if (event.hasBuyer(buyer)) {
            throw new IllegalStateException("Buyer has already booked this event");
        }
        event.addBuyer(buyer);
        return EventResponse.from(event);
    }

    public EventResponse inviteSeller(Long entertainerId, Long eventId, Long sellerId) {
        Event event = getEventForManagement(entertainerId, eventId);
        ensureEventIsActive(event);

        Seller seller = getSeller(sellerId);

        if (event.isSellerConfirmed(seller)) {
            throw new IllegalStateException("Seller has already accepted this invitation");
        }

        if (!event.isSellerInvited(seller)) {
            event.inviteSeller(seller);
        }

        return EventResponse.from(event);
    }

    public EventResponse confirmSellerParticipation(Long eventId, Long sellerId) {
        Event event = getEventById(eventId);
        ensureEventIsActive(event);

        Seller seller = getSeller(sellerId);

        if (!event.isSellerInvited(seller)) {
            throw new IllegalStateException("Seller has not been invited to this event");
        }

        if (!event.isSellerConfirmed(seller)) {
            event.confirmSeller(seller);
        }

        return EventResponse.from(event);
    }


    public EventResponse inviteByEmail(Long entertainerId, Long eventId, InviteByEmail request) {
        Event event = getEventForManagement(entertainerId, eventId);
        ensureEventIsActive(event);

        String email = request.getEmail().trim();

        Seller seller = getSellerByEmail(email);

        if (seller != null) {
            if (event.isSellerConfirmed(seller)) {
                throw new IllegalStateException("Seller has already accepted this invitation");
            }

            if (!event.isSellerInvited(seller)) {
                event.inviteSeller(seller);
            }

            return EventResponse.from(event);
        }

        User user = getUserByEmail(email);

        if (user instanceof Buyer) {
            throw new IllegalStateException("I buyer possono acquistare autonomamente gli eventi e non necessitano di un invito");
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "L'utente con email " + email + " non è un seller e non può essere invitato all'evento"
        );
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalStateException("Event with id " + eventId + " does not exist"));
    }

    private Event getEventForManagement(Long userId, Long eventId) {
        Event event = getEventById(eventId);
        ensureOwnership(event, userId);
        return event;
    }

    private void ensureOwnership(Event event, Long userId) {
        if (event.getEntertainer() == null || !Objects.equals(event.getEntertainer().getId(), userId)) {
            throw new IllegalStateException("User " + userId + " cannot manage this event");
        }
    }

    private void validateChronology(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Both start and end date must be provided");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Event end date cannot be before the start date");
        }
    }

    private void ensurePositivePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Event price cannot be negative");
        }
    }

    private void ensureEventIsActive(Event event) {
        if (event.getState() == EventState.CANCELLED || event.getState() == EventState.ARCHIVED) {
            throw new IllegalStateException("Cannot book or invite for an event that is not active");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User with email " + email + " does not exist"));
    }

    private Seller getSeller(Long sellerId) {
        User user = getUserById(sellerId);
        return toSeller(user, () -> "User with id " + sellerId + " is not a seller");
    }

    private Seller getSellerByEmail(String email) {
        User user = getUserByEmail(email);
        if (user instanceof Seller seller && SELLER_ROLES.contains(user.getSimpleRole())) {
            return seller;
        }
        return null;
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User with id " + userId + " does not exist"));
    }

    private <T extends User> T getUser(Long userId, Class<T> type) {
        User user = getUserById(userId);
        if (!type.isInstance(user)) {
            throw new IllegalStateException("User with id " + userId + " is not a " + type.getSimpleName());
        }
        return type.cast(user);
    }

    private Seller toSeller(User user, Supplier<String> errorMessageSupplier) {
        if (user instanceof Seller seller && SELLER_ROLES.contains(user.getSimpleRole())) {
            return seller;
        }
        throw new IllegalStateException(errorMessageSupplier.get());
    }
}