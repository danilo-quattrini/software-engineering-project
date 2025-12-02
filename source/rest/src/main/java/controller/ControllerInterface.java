package controller;

import java.util.List;
import java.util.Optional;

public interface ControllerInterface<E, D> {
    List<E> getAll();
    Optional<E> get(Long id);
    void save(E entity);
    void delete(Long id);
    void update(Long id, D dto);
}
