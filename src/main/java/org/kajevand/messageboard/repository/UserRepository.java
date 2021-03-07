package org.kajevand.messageboard.repository;

import org.kajevand.messageboard.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    List<User> findByUsernameIgnoreCase(String username);
}
