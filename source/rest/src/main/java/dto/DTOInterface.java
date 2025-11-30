package dto;

public interface DTOInterface<E, D> {
    D toDTO(E entity);
    E fromDTO();
}
