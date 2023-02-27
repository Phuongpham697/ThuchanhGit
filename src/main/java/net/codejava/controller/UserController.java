package net.codejava.controller;

import net.codejava.model.User;
import net.codejava.service.serviceimpl.UserServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserServiceImpl userService;
    @Value("${upload.path}")
    private String fileUpload;

    @RequestMapping("/")
    public String viewHomePage(Model model) {

        return viewPage(model, 1);
    }

    public String viewPage(Model model, @PathVariable(name = "pageNum") int pageNum) {
        Page<User> pageUser = userService.listAll(pageNum);
        List<User> listUser = pageUser.getContent();
        model.addAttribute("listUsers", listUser);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("totalPages", pageUser.getTotalPages());
        model.addAttribute("totalItems", pageUser.getTotalElements());

        return "index";
    }

    @RequestMapping("/search")
    public String ViewSearch(Model model, @Param("keyword") String keyword) {
        List<User> listUser = userService.listSearch(keyword);
        model.addAttribute("listUser", listUser);
        model.addAttribute("keyword", keyword);
        return "index";
    }

    @RequestMapping("/login")
    public String viewLogin(Model model, HttpSession httpSession) {
        //        check session
        if (httpSession.getAttribute("user") != null) return "redirect:/";

        User user = new User();
        model.addAttribute(user);
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)

    public String checkLogin(@ModelAttribute("user") User user, Model model, HttpSession httpSession) {
//        set password md5
        user.setPassword(DigestUtils.md5Hex(user.getPassword().trim()).toUpperCase());
        if (!userService.checkUser(user.getEmail().trim(), user.getPassword().trim())) {
            String msg = "đăng nhâp thất bại";
            model.addAttribute("msg", msg);
            return "login";
        }
        httpSession.setAttribute("user", user.getEmail());
        return "redirect:/";

    }

    @RequestMapping("/new")
    public String newUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "new_user";
    }

    private static final Path CURRENT_FOLDER = Paths.get(System.getProperty("user.dir"));

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveUser(@ModelAttribute("user") User user, @RequestParam MultipartFile image) throws Exception {
        Path staticPath = Paths.get("static");
        Path imagePath = Paths.get("images");
        if (!Files.exists(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath))) {
            Files.createDirectories(CURRENT_FOLDER.resolve(staticPath).resolve(imagePath));
        }
        if(!image.isEmpty())
        {
            Path file = CURRENT_FOLDER.resolve(staticPath).resolve(imagePath).resolve(image.getOriginalFilename());
//      set name photo
            user.setPhoto(image.getOriginalFilename());

            try (OutputStream os = Files.newOutputStream(file)) {
                os.write(image.getBytes());
            }
        }
//      encoding password md5 & set password md5
        user.setPassword(DigestUtils.md5Hex(user.getPassword().trim()).toUpperCase());
        userService.save(user);
        return "redirect:/";
    }

    @RequestMapping("/edit/{id}")
    public ModelAndView showEditUserPage(@PathVariable(name = "id") long id) {
        ModelAndView mav = new ModelAndView("edit_user");
        User user = userService.get(id);
        mav.addObject(user);
        return mav;
    }

    @RequestMapping("/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") long id) {
        userService.delete(id);
        return "redirect:/";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/login";
    }

}
