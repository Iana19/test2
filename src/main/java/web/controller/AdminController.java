package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import web.dao.RoleDAO;
import web.dao.UserDAO;
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
    private final RoleDAO roleDAO;

    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AdminController(UserService userService, RoleDAO roleDAO) {
        this.userService = userService;
        this.roleDAO = roleDAO;
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
        HashSet<Role> Setroles = new HashSet<>();
        Role role_admin = roleDAO.createRoleIfNotFound("ADMIN", 1L);
        Role role_user = roleDAO.createRoleIfNotFound("USER", 2L);
        Setroles.add(role_admin);
        Setroles.add(role_user);
        modelAndView.addObject("rolelist", Setroles);
        return modelAndView;
    }

    @PostMapping(value = "/admin/edit")
    public String editUser(
            @ModelAttribute("id") Long id,
            @ModelAttribute("name") String name,
            @ModelAttribute("password") String password,
            @ModelAttribute("lastname") String lastname,
            @ModelAttribute("age") byte age,
            @ModelAttribute("city") String city,
            @RequestParam("roles") String[] roles
    ) {
        User user = userService.getById(id);
        user.setName(name);
        user.setLastname(lastname);
        user.setAge(age);
        user.setCity(city);
        if (!password.isEmpty()) {
            user.setPassword(password);
        }
        Set<Role> Setroles = new HashSet<>();
        for (String st : roles) {
            if (st.equals("ADMIN")) {
                Role role_admin = roleDAO.createRoleIfNotFound("ADMIN", 1L);
                Setroles.add(role_admin);
            }
            if (st.equals("USER")) {
                Role role_user = roleDAO.createRoleIfNotFound("USER", 2L);
                Setroles.add(role_user);
            }
        }
        user.setRoles(Setroles);
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
