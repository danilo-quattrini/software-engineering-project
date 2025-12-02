package it.unicam.cs.ids2425.events;

import it.unicam.cs.ids2425.events.dto.EventRequest;
import it.unicam.cs.ids2425.location.Location;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/christian/events")
public class EventPageController {

    private final EventService eventService;


    public EventPageController(EventService eventService) {
        this.eventService = eventService;
    }

    @PreAuthorize("hasRole('ENTERTAINER')")
    @PostMapping("/new")
    public String handleCreateEvent(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("type") EventType type,
            @RequestParam("lat") Double lat,
            @RequestParam("lng") Double lng,
            @RequestParam("locationName") String locationName,
            @RequestParam("startDateTime") String startDateTime,
            @RequestParam("endDateTime") String endDateTime,
            @RequestParam("price") BigDecimal price,
            Authentication authentication
    ) {
        if (lat == null || lng == null || locationName == null || locationName.isBlank()) {
            return "redirect:/events/new?error=locationMissing";
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        Location eventLocation = new Location();
        eventLocation.setLat(lat);
        eventLocation.setLng(lng);
        eventLocation.setAddress(locationName);

        Long amount = price.longValue();

        EventRequest req = new EventRequest(
                title,
                description,
                type,
                eventLocation,
                LocalDateTime.parse(startDateTime, fmt),
                LocalDateTime.parse(endDateTime, fmt),
                amount,
                "EUR"
        );

        eventService.createEvent(authentication.getName(), req);

        return "redirect:/events";
    }
}
