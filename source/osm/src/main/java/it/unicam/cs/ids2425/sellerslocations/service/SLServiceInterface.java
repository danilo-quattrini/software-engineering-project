package it.unicam.cs.ids2425.sellerslocations.service;

import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.sellerslocations.SLDTO;
import service.ServiceInterface;
import it.unicam.cs.ids2425.users.roles.seller.Seller;

import java.util.List;

public interface SLServiceInterface extends ServiceInterface<Location, SLDTO> {
    List<Seller> getAllSellers();
}
