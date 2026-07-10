package com.laundry.laundryforu.controller;

import com.laundry.laundryforu.model.User;
import com.laundry.laundryforu.model.Order;
import com.laundry.laundryforu.repository.UserRepository;
import com.laundry.laundryforu.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LaundryController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public LaundryController(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/daftar")
    public String daftar() {
        return "daftar";
    }

    @PostMapping("/daftar")
    public String daftarUser(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        if (userRepository.findByEmail(email) != null) return "redirect:/daftar?error=email";
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("user");
        userRepository.save(user);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, HttpSession session) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "admin".equalsIgnoreCase(user.getRole()) ? "redirect:/admin" : "redirect:/home";
        }
        return "redirect:/login?error=true";
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("orders", orderRepository.findByUserId(user.getId()));
        return "home";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        model.addAttribute("orders", orderRepository.findByUserId(user.getId()));
        return "profile";
    }

    @GetMapping("/buat-pesanan")
    public String buatPesanan(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("order", new Order());
        return "buat-pesanan";
    }

    @PostMapping("/order/simpan")
    public String simpanOrder(@ModelAttribute Order order, HttpSession session,
                              @RequestParam("jenisLayanan") String jenisLayananParam,
                              @RequestParam(defaultValue = "0") double antarJemput) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        order.setUser(user);
        order.setAntarJemput(antarJemput);

        String namaLayanan = jenisLayananParam;
        double hargaPerSatuan = 0;
        if (jenisLayananParam != null && jenisLayananParam.contains(" - ")) {
            String[] parts = jenisLayananParam.split(" - ", 2);
            namaLayanan = parts[0];
            hargaPerSatuan = Double.parseDouble(parts[1]);
        } else if (jenisLayananParam != null) {
            hargaPerSatuan = Double.parseDouble(jenisLayananParam);
        }
        order.setJenisLayanan(namaLayanan);
        if (order.getJumlahItem() != null && order.getJumlahItem() > 0) {
            order.setTotalHarga((hargaPerSatuan * order.getJumlahItem()) + antarJemput);
        } else if (order.getBerat() != null && order.getBerat() > 0) {
            order.setTotalHarga((hargaPerSatuan * order.getBerat()) + antarJemput);
        } else {
            order.setTotalHarga(antarJemput);
        }
        order.setStatus("Belum Diterima");
        orderRepository.save(order);
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
