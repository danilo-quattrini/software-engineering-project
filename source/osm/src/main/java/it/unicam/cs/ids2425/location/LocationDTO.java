package it.unicam.cs.ids2425.location;

import dto.DTOInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LocationDTO implements DTOInterface<Location, LocationDTO> {
    private Double lat;
    private Double lng;
    private String address;

    @Override
    public LocationDTO toDTO(Location entity) {
        LocationDTO dto = new LocationDTO();
        dto.setLat(entity.getLat());
        dto.setLng(entity.getLng());
        dto.setAddress(entity.getAddress());
        return dto;
    }

    @Override
    public Location fromDTO() {
        return new Location(this.lat, this.lng, this.address);
    }
}
