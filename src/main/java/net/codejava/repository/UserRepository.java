package net.codejava.repository;

import net.codejava.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
@Query("SELECT p FROM User p WHERE CONCAT(' ',p.fullname,p.email,p.photo,' ',p.birthday,p.gender,p.profession,' ',' ') LIKE %?1%")
   public List<User> search(String keyword);
@Query("SELECT p From User p Where p.email=?1 AND p.password=?2")
   public User searchUser(String email,String password);
}
