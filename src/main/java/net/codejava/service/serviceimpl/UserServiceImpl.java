package net.codejava.service.serviceimpl;

import net.codejava.model.User;
import net.codejava.repository.UserRepository;
import net.codejava.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repo;

    @Override
    public Page<User> listAll(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, 5);
        return repo.findAll(pageable);
    }

    @Override
    public List<User> listSearch(String keyword) {
        if(keyword!=null)
            return repo.search(keyword);
        return repo.findAll();
    }
    @Override
    public boolean checkUser(String email,String password)
    {
        if(repo.searchUser(email,password)==null)
        return false;
        return true;

    }
    @Override
    public void save(User user) {
        repo.save(user);
    }

    @Override
    public User get(long id) {
        return repo.findById(id).get();
    }

    @Override
    public void delete(long id) {
        repo.deleteById(id);
    }
}
