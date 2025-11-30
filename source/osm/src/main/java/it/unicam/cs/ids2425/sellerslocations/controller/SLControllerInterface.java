package it.unicam.cs.ids2425.sellerslocations.controller;

import controller.ControllerInterface;
import it.unicam.cs.ids2425.location.Location;
import it.unicam.cs.ids2425.sellerslocations.SLDTO;
import it.unicam.cs.ids2425.users.roles.seller.Seller;

import java.util.List;

public interface SLControllerInterface extends ControllerInterface<Location, SLDTO> {
    List<Seller> getAllSellers();
}
