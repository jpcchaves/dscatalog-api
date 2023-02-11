package com.ds.dscatalog.services;

import com.ds.dscatalog.dto.CategoryDTO;
import com.ds.dscatalog.dto.ProductDTO;
import com.ds.dscatalog.entities.Category;
import com.ds.dscatalog.entities.Product;
import com.ds.dscatalog.repositories.CategoryRepository;
import com.ds.dscatalog.repositories.ProductRepository;
import com.ds.dscatalog.services.exceptions.DatabaseException;
import com.ds.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);

        return list.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Não foi encontrado uma categoria com o ID informado: " + id));

        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {

        Product entity = new Product();

        copyDtoToEntity(dto, entity);


        entity = repository.save(entity);
        return new ProductDTO(entity);

    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {

            Product entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);

            entity = repository.save(entity);

            return new ProductDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Não foi possível atualizar a categoria pois o ID informado não existe: " + dto.getId());

        }
    }

    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Não foi possível excluir a categoria pois o ID informado não existe: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Não foi possível deletar a categoria pois acarretaria em uma inconsistência na integridade do banco de dados");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        entity.getCategories().clear();

        for (CategoryDTO categoryDTO : dto.getCategories()) {

            Category category = categoryRepository.getReferenceById(categoryDTO.getId());

            entity.getCategories().add(category);

        }
    }
}
