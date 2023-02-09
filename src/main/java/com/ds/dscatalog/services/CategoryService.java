package com.ds.dscatalog.services;

import com.ds.dscatalog.dto.CategoryDTO;
import com.ds.dscatalog.entities.Category;
import com.ds.dscatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

}
