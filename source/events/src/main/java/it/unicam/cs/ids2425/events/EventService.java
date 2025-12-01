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
                .orElseThrow(() -> notFound("Non abbiamo trovato alcun utente registrato con l'email " + email));

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
            throw badRequest("Hai già prenotato questo evento");
        }
        event.addBuyer(buyer);
        return EventResponse.from(event);
    }

    public EventResponse inviteSeller(Long entertainerId, Long eventId, Long sellerId) {
        Event event = getEventForManagement(entertainerId, eventId);
        ensureEventIsActive(event);

        Seller seller = getSeller(sellerId);

        if (event.isSellerConfirmed(seller)) {
            throw badRequest("Il venditore ha già accettato l'invito");
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
            throw badRequest("Il venditore non è stato invitato a questo evento");
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
                throw badRequest("Il venditore ha già accettato l'invito");
            }

            if (!event.isSellerInvited(seller)) {
                event.inviteSeller(seller);
            }

            return EventResponse.from(event);
        }

        User user = getUserByEmail(email);

        if (user instanceof Buyer) {
            throw badRequest("I buyer possono acquistare autonomamente gli eventi e non necessitano di un invito");
        }

        throw badRequest("L'utente con email " + email + " non è un seller e non può essere invitato all'evento");
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> notFound("Non abbiamo trovato l'evento richiesto (id " + eventId + ")"));
    }

    private Event getEventForManagement(Long userId, Long eventId) {
        Event event = getEventById(eventId);
        ensureOwnership(event, userId);
        return event;
    }

    private void ensureOwnership(Event event, Long userId) {
        if (event.getEntertainer() == null || !Objects.equals(event.getEntertainer().getId(), userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Non hai i permessi per gestire questo evento"
            );
        }
    }

    private void validateChronology(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw badRequest("Devi fornire sia la data di inizio sia quella di fine");
        }
        if (end.isBefore(start)) {
            throw badRequest("La data di fine evento non può precedere quella di inizio");
        }
    }

    private void ensurePositivePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw badRequest("Il prezzo dell'evento deve essere positivo");
        }
    }

    private void ensureEventIsActive(Event event) {
        if (event.getState() == EventState.CANCELLED || event.getState() == EventState.ARCHIVED) {
            throw badRequest("Non è possibile prenotare o invitare per un evento non attivo");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> notFound("Non abbiamo trovato alcun utente registrato con l'email " + email));
    }

    private Seller getSeller(Long sellerId) {
        User user = getUserById(sellerId);
        return toSeller(user, () -> "L'utente con id " + sellerId + " non è un seller e non può essere invitato");
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
                .orElseThrow(() -> notFound("Non abbiamo trovato l'utente richiesto (id " + userId + ")"));
    }

    private <T extends User> T getUser(Long userId, Class<T> type) {
        User user = getUserById(userId);
        if (!type.isInstance(user)) {
            throw badRequest("L'utente con id " + userId + " non è un " + type.getSimpleName());
        }
        return type.cast(user);
    }

    private Seller toSeller(User user, Supplier<String> errorMessageSupplier) {
        if (user instanceof Seller seller && SELLER_ROLES.contains(user.getSimpleRole())) {
            return seller;
        }
        throw badRequest(errorMessageSupplier.get());
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}