package com.campus.repository;


import java.util.List;
import java.util.Optional;

/**
 * Base repository interface defining common CRUD operations
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface BaseRepository<T, ID> {
  /**
   * Save an entity
   * @param entity The entity to save
   * @return The saved entity
   */
  T save(T entity);

  /**
   * Update an entity
   * @param entity The entity to update
   * @return The updated entity
   */
  T update(T entity);

  /**
   * Delete an entity by ID
   * @param id The entity ID
   * @return true if deleted, false otherwise
   */
  boolean deleteById(ID id);

  /**
   * Find an entity by ID
   * @param id The entity ID
   * @return Optional containing the entity if found
   */
  Optional<T> findById(ID id);

  /**
   * Find all entities
   * @return List of all entities
   */
  List<T> findAll();
}