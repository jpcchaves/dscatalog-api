package com.ds.dscatalog.services;

import com.ds.dscatalog.dto.CategoryDTO;
import com.ds.dscatalog.entities.Category;
import com.ds.dscatalog.repositories.CategoryRepository;
import com.ds.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> list = repository.findAll();

        List<CategoryDTO> listDto = list.stream().map(cat -> new CategoryDTO(cat)).collect(Collectors.toList());

        return listDto;
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado uma categoria com o ID informado: " + id));

        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {

        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDTO(entity);

    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {

            Category entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = repository.save(entity);

            return new CategoryDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Não foi possível atualizar a categoria pois o ID informado não existe: " + dto.getId());

        }
    }
}
