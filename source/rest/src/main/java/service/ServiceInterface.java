package service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface ServiceInterface<E, D> {
    List<E> getAll();
    Optional<E> get(Long id);
    void save(E entity);
    void delete(Long id);
    void update(Long id, D dto);

    default <F> boolean updateField(F newValue, F oldValue, Consumer<F> setter) {
        if (newValue == null) return false;
        if (newValue instanceof String s && s.isEmpty()) return false;
        if (!newValue.equals(oldValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }
}
