package dashboard.admin.controller;

import dashboard.admin.entity.User;
import dashboard.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute User user, BindingResult br) {
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("user", userRepository.findById(id).orElse(new User()));
        return "admin/user-form";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
