package net.codejava.service;

import net.codejava.model.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    public Page<User> listAll(int pageNum);
    public List<User> listSearch(String keyword);
    public boolean checkUser(String email,String password);
    public void save(User user);
    public User get(long id);
    public void delete(long id);


}
