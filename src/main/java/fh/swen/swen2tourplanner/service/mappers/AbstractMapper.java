package fh.swen.swen2tourplanner.service.mappers;

import java.util.List;

public abstract class AbstractMapper<E, D> {

    public abstract D mapToDTO(E entity);

    public abstract E mapToEntity(D dto);

    public abstract void updateEntity(E entity, D dto);

    public List<D> mapToDTOList(List<E> entities) {
        return entities.stream().map(this::mapToDTO).toList();
    }

    public List<E> mapToEntityList(List<D> dtos) {
        return dtos.stream().map(this::mapToEntity).toList();
    }
}
