package it.unicam.cs.ids2425.sellerslocations;

import dto.DTOInterface;
import it.unicam.cs.ids2425.location.LocationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import it.unicam.cs.ids2425.users.roles.seller.Seller;

@NoArgsConstructor
@Getter
@Setter
public class SLDTO implements DTOInterface<Seller, SLDTO> {
    private LocationDTO location;

    @Override
    public SLDTO toDTO(Seller entity) {
        SLDTO dto = new SLDTO();
        dto.setLocation(location.toDTO(entity.getLocation()));
        return dto;
    }

    @Override
    public Seller fromDTO() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
