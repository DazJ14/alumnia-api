package io.github.dazj14.alumnia_api.service;

import io.github.dazj14.alumnia_api.dto.CreateMateriaRequest; // Importar el DTO
import io.github.dazj14.alumnia_api.exception.ResourceNotFoundException;
import io.github.dazj14.alumnia_api.model.Materia;
import io.github.dazj14.alumnia_api.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.github.dazj14.alumnia_api.dto.UpdateMateriaRequest; // Importar DTO

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;

    public List<Materia> findAll() {
        return materiaRepository.findAll();
    }

    public Materia findById(Integer id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + id));
    }

    public Materia create(CreateMateriaRequest request) {
        Materia nuevaMateria = new Materia();
        nuevaMateria.setClaveMateria(request.getClaveMateria());
        nuevaMateria.setNombreMateria(request.getNombreMateria());
        nuevaMateria.setCreditos(request.getCreditos());

        return materiaRepository.save(nuevaMateria);
    }

    public Materia update(Integer id, UpdateMateriaRequest request) {
        Materia materiaExistente = findById(id);

        materiaExistente.setClaveMateria(request.getClaveMateria());
        materiaExistente.setNombreMateria(request.getNombreMateria());
        materiaExistente.setCreditos(request.getCreditos());

        return materiaRepository.save(materiaExistente);
    }

    public void deleteById(Integer id) {
        if (!materiaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Materia no encontrada con ID: " + id);
        }
        materiaRepository.deleteById(id);
    }
}