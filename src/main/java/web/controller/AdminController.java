package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import web.dao.RoleDao;
import web.model.Role;
import web.model.User;
import web.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/")
public class AdminController {
    private final UserService userService;
    private final RoleDao roleDao;

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AdminController(UserService userService, RoleDao roleDao) {
        this.userService = userService;
        this.roleDao = roleDao;
    }

    @GetMapping("/admin")
    public ModelAndView allUsers() {
        List<User> users = userService.allUsers();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin");
        modelAndView.addObject("usersList", users);
        return modelAndView;
    }

    @GetMapping(value = "/admin/add")
    public String addPage() {
        return "addUser";
    }

    @PostMapping(value = "/admin/add")
    public String addUser(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/edit/{id}")
    public ModelAndView editPage(@PathVariable("id") long id) {
        User user = userService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("adminEditUser");
        modelAndView.addObject("user", user);
        HashSet<Role> setRoles = new HashSet<>();
        Role roleOfAdmin = roleDao.createRoleIfNotFound("ADMIN", 1L);
        Role roleOfUser = roleDao.createRoleIfNotFound("USER", 2L);
        setRoles.add(roleOfAdmin);
        setRoles.add(roleOfUser);
        modelAndView.addObject("rolelist", setRoles);
        return modelAndView;
    }

    @PostMapping(value = "/admin/edit")
    public String editUser(
            @ModelAttribute("id") Long id,
            @ModelAttribute("name") String name,
            @ModelAttribute("password") String password,
            @ModelAttribute("lastname") String lastname,
            @ModelAttribute("age") byte age,
            @RequestParam("roles") String[] roles
    ) {
        User user = userService.getById(id);
        user.setName(name);
        user.setLastname(lastname);
        user.setAge(age);
        if (!password.isEmpty()) {
            user.setPassword(password);
        }
        Set<Role> setRoles = new HashSet<>();
        for (String st : roles) {
            if (st.equals("ADMIN")) {
                Role roleOfAdmin = roleDao.createRoleIfNotFound("ADMIN", 1L);
                setRoles.add(roleOfAdmin);
            }
            if (st.equals("USER")) {
                Role roleOfUser = roleDao.createRoleIfNotFound("USER", 2L);
                setRoles.add(roleOfUser);
            }
        }
        user.setRoles(setRoles);
        userService.save(user);
        return "redirect:/admin";
    }

    @GetMapping(value = "/admin/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        User user = userService.getById(id);
        userService.delete(user);
        return "redirect:/admin";
    }

}