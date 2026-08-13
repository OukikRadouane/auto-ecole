package com.auto.tutorial.repository;

import com.auto.tutorial.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByName(String name);
    List<Category> findAllByOrderByDisplayOrderAsc();
    Boolean existsByName(String name);

}
