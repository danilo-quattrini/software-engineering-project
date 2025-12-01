package it.unicam.cs.ids2425.events;

import it.unicam.cs.ids2425.events.dto.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/api")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ENTERTAINER')")
    public EventResponse createEvent(Authentication authentication,
                                     @Valid @RequestBody EventRequest request) {
        return eventService.createEvent(authentication.getName(), request);
    }

    @PutMapping("/{eventId}/entertainers/{entertainerId}")
    @PreAuthorize("hasRole('ENTERTAINER')")
    public EventResponse updateEvent(@PathVariable("entertainerId") Long entertainerId,
                                     @PathVariable("eventId") Long eventId,
                                     @Valid @RequestBody EventUpdateRequest request) {
        return eventService.updateEvent(entertainerId, eventId, request);
    }

    @PatchMapping("/{eventId}/entertainers/{entertainerId}/state")
    @PreAuthorize("hasRole('ENTERTAINER')")
    public EventResponse updateEventState(@PathVariable("entertainerId") Long entertainerId,
                                          @PathVariable("eventId") Long eventId,
                                          @Valid @RequestBody EventStateUpdateRequest request) {
        return eventService.updateState(entertainerId, eventId, request);
    }

    @DeleteMapping("/{eventId}/entertainers/{entertainerId}")
    @PreAuthorize("hasRole('ENTERTAINER')")
    public void deleteEvent(@PathVariable("entertainerId") Long entertainerId,
                            @PathVariable("eventId") Long eventId) {
        eventService.deleteEvent(entertainerId, eventId);
    }

    @GetMapping
    public List<EventResponse> getEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{eventId}")
    public EventResponse getEvent(@PathVariable("eventId") Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/{eventId}/buyers/{buyerId}/bookings")
    @PreAuthorize("hasRole('BUYER')")
    public EventResponse bookEvent(@PathVariable("eventId") Long eventId,
                                   @PathVariable("buyerId") Long buyerId) {
        return eventService.bookEvent(eventId, buyerId);
    }

    @PostMapping("/{eventId}/entertainers/{entertainerId}/sellers/{sellerId}/invite")
    @PreAuthorize("hasRole('ENTERTAINER')")
    public EventResponse inviteSeller(@PathVariable("entertainerId") Long entertainerId,
                                      @PathVariable("eventId") Long eventId,
                                      @PathVariable("sellerId") Long sellerId) {
        return eventService.inviteSeller(entertainerId, eventId, sellerId);
    }

    @PostMapping("/{eventId}/sellers/{sellerId}/confirm")
    @PreAuthorize("hasAnyRole('PRODUCER','DISTRIBUTOR','TRANSFORMER')")
    public EventResponse confirmSeller(@PathVariable("eventId") Long eventId,
                                       @PathVariable("sellerId") Long sellerId) {
        return eventService.confirmSellerParticipation(eventId, sellerId);
    }

    @PostMapping("/{eventId}/entertainers/{entertainerId}/invite-by-email")
    @PreAuthorize("hasRole('ENTERTAINER')")
    public EventResponse inviteByEmail(
            @PathVariable("eventId") Long eventId,
            @PathVariable("entertainerId") Long entertainerId,
            @RequestBody InviteByEmail request
    ) {
        return eventService.inviteByEmail(entertainerId, eventId, request);
    }

}
