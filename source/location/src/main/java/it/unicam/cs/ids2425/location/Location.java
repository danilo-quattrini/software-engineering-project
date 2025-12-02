package it.unicam.cs.ids2425.location;

import jakarta.persistence.Embeddable;
import lombok.*;

@NoArgsConstructor
@Getter
@ToString
@Embeddable
public class Location {
    private Double lat;
    private Double lng;
    @Setter
    private String address;

    public Location(double lat, double lng, String address) {
        if(lat<-90 || lat>90)
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        if(lng<-180 || lng>180)
            throw new IllegalArgumentException("Longitude must be between -180 and 180");

        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public void setLat(double lat) {
        if(lat<-90 || lat>90)
            throw new IllegalArgumentException("Latitude must be between -90 and 90");

        this.lat = lat;
    }

    public void setLng(double lng) {
        if(lng<-180 || lng>180)
            throw new IllegalArgumentException("Longitude must be between -180 and 180");

        this.lng = lng;
    }
}
