package com.laundry.laundryforu.controller;

import com.laundry.laundryforu.model.Layanan;
import com.laundry.laundryforu.model.Order;
import com.laundry.laundryforu.model.User;
import com.laundry.laundryforu.repository.LayananRepository;
import com.laundry.laundryforu.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin") 
public class AdminController {

    private final OrderRepository orderRepository;
    private final LayananRepository layananRepository;

    public AdminController(OrderRepository orderRepository,
                          LayananRepository layananRepository) {
        this.orderRepository = orderRepository;
        this.layananRepository = layananRepository;
    }

    // --- DASHBOARD ---
    @GetMapping({"", "/dashboard"})
    public String adminPage(Model model) {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("orders", orders);
        model.addAttribute("statusBadge", Map.ofEntries(
            Map.entry("Selesai", "bg-green-100 text-green-700"),
            Map.entry("Laundry Mu Siap Diantar", "bg-green-100 text-green-700"),
            Map.entry("Ditolak", "bg-red-100 text-red-700"),
            Map.entry("Belum Diterima", "bg-yellow-100 text-yellow-700"),
            Map.entry("Kurir Menjemput Pakaian Anda", "bg-purple-100 text-purple-700"),
            Map.entry("Sedang Dicuci", "bg-blue-100 text-blue-700"),
            Map.entry("Sedang Disetrika", "bg-pink-100 text-pink-700"),
            Map.entry("Sedang Dikemas", "bg-cyan-100 text-cyan-700"),
            Map.entry("Sudah Diterima", "bg-indigo-100 text-indigo-700")
        ));
        double totalPendapatan = orders.stream()
                .mapToDouble(order -> order.getTotalHarga() != null ? order.getTotalHarga() : 0.0)
                .sum();
        model.addAttribute("totalPendapatan", totalPendapatan);
        long aktif = orders.stream().filter(o -> "Belum Diterima".equalsIgnoreCase(o.getStatus())
                || "Sudah Diterima".equalsIgnoreCase(o.getStatus())
                || "Kurir Menjemput Pakaian Anda".equalsIgnoreCase(o.getStatus())
                || "Sedang Dicuci".equalsIgnoreCase(o.getStatus())
                || "Sedang Disetrika".equalsIgnoreCase(o.getStatus())
                || "Sedang Dikemas".equalsIgnoreCase(o.getStatus())
                || "Selesai".equalsIgnoreCase(o.getStatus())
                || "Laundry Mu Siap Diantar".equalsIgnoreCase(o.getStatus())).count();
        model.addAttribute("jumlahPesananBaru", aktif);
        model.addAttribute("jumlahLayanan", layananRepository.count());
        long pendingKonfirmasi = orders.stream().filter(o -> "Belum Diterima".equalsIgnoreCase(o.getStatus())).count();
        model.addAttribute("pendingKonfirmasi", pendingKonfirmasi);
        long pendingPembayaran = orders.stream().filter(o -> "Transfer Bank".equalsIgnoreCase(o.getMetodePembayaran())).count();
        model.addAttribute("pendingPembayaran", pendingPembayaran);
        return "Admin/AdminHome";
    }

    // --- MENU DAFTAR PESANAN ---
    @GetMapping("/pesanan")
    public String daftarPesanan(Model model) {
        model.addAttribute("orders", orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "Admin/daftar-pesanan";
    }

    // --- MENU LAYANAN ---
    @GetMapping("/layanan")
    public String layananPage(Model model,
                             @RequestParam(value = "edit", required = false) Long editId) {
        model.addAttribute("layananList", layananRepository.findAll());
        if (editId != null) {
            model.addAttribute("layanan", layananRepository.findById(editId).orElse(null));
        } else {
            model.addAttribute("layanan", null);
        }
        return "Admin/layanan";
    }

    @PostMapping("/layanan/tambah")
    public String tambahLayanan(@RequestParam String namaLayanan,
                               @RequestParam int harga,
                               @RequestParam(required = false) String deskripsi) {
        Layanan layanan = new Layanan();
        layanan.setNamaLayanan(namaLayanan);
        layanan.setHarga(harga);
        layanan.setDeskripsi(deskripsi);
        layananRepository.save(layanan);
        return "redirect:/admin/layanan";
    }

    @PostMapping("/layanan/update/{id}")
    public String updateLayanan(@PathVariable Long id,
                               @RequestParam String namaLayanan,
                               @RequestParam int harga,
                               @RequestParam(required = false) String deskripsi) {
        Layanan layanan = layananRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Layanan tidak ditemukan"));
        layanan.setNamaLayanan(namaLayanan);
        layanan.setHarga(harga);
        layanan.setDeskripsi(deskripsi);
        layananRepository.save(layanan);
        return "redirect:/admin/layanan";
    }

    @GetMapping("/layanan/edit/{id}")
    public String editLayanan(@PathVariable Long id, Model model) {
        return "redirect:/admin/layanan?edit=" + id;
    }

    @GetMapping("/layanan/hapus/{id}")
    public String hapusLayanan(@PathVariable Long id) {
        layananRepository.deleteById(id);
        return "redirect:/admin/layanan";
    }
    // --- TAMBAH PESANAN ---
    @GetMapping("/tambah-pesanan")
    public String formTambah(Model model) {
        model.addAttribute("order", new Order());
        return "Admin/admin-tambah-pesanan"; 
    }

    @PostMapping("/simpan-pesanan")
    public String simpanPesanan(@ModelAttribute("order") Order order,
                                @RequestParam(required = false) Double hargaLayanan,
                                @RequestParam(defaultValue = "0") double antarJemput,
                                HttpSession session) {
        if (order.getTotalHarga() == null) {
            if (hargaLayanan != null) {
                double subtotal = 0;
                if (order.getJumlahItem() != null && order.getJumlahItem() > 0) {
                    subtotal = hargaLayanan * order.getJumlahItem();
                }
                if (order.getBerat() != null && order.getBerat() > 0) {
                    subtotal += hargaLayanan * order.getBerat();
                }
                order.setTotalHarga(subtotal + antarJemput);
            } else {
                order.setTotalHarga(antarJemput);
            }
        }
        if (order.getStatus() == null) order.setStatus("Belum Diterima");
        if (order.getMetodePembayaran() == null) order.setMetodePembayaran("Bayar di Tempat");
        if (order.getCatatan() == null) order.setCatatan("");
        User user = (User) session.getAttribute("user");
        if (user != null) order.setUser(user);
        orderRepository.save(order);
        return "redirect:/admin";
    }

    // --- TAMBAH PESANAN ---
@GetMapping("/edit/{id}")
public String editOrderPage(@PathVariable("id") Long id, Model model) {
    Order order = orderRepository.findById(id).orElse(null);
    
    if (order == null) return "redirect:/admin";
    
    // BERI NILAI DEFAULT UNTUK SEMUA FIELD YANG ADA DI FORM HTML
    if (order.getTotalHarga() == null) order.setTotalHarga(0.0);
    if (order.getJumlahItem() == null) order.setJumlahItem(0);
    if (order.getBerat() == null) order.setBerat(0.0);
    if (order.getNamaLengkap() == null) order.setNamaLengkap("");
    if (order.getNoWa() == null) order.setNoWa("");
    if (order.getAlamat() == null) order.setAlamat("");
    if (order.getJenisLayanan() == null) order.setJenisLayanan("");
    if (order.getStatus() == null) order.setStatus("Belum Diterima");
    if (order.getAntarJemput() == null) order.setAntarJemput(0.0);
    if (order.getMetodePembayaran() == null) order.setMetodePembayaran("Bayar di Tempat");
    if (order.getCatatan() == null) order.setCatatan("");
    if (order.getTanggalJemput() == null) order.setTanggalJemput(LocalDate.now());
    
    model.addAttribute("order", order);
    return "edit-pesanan";
}

    @PostMapping("/update-order")
    public String updateOrder(@ModelAttribute Order order,
                              @RequestParam(defaultValue = "0") double hargaSatuan,
                              @RequestParam("jenisLayanan") String jenisLayananParam,
                              @RequestParam(defaultValue = "0") double antarJemput,
                              @RequestParam(name = "totalHarga", defaultValue = "0") double totalHargaInput) {

        Order existing = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));

        String namaLayanan = jenisLayananParam;
        double hargaPerSatuan = hargaSatuan;
        if (jenisLayananParam != null && jenisLayananParam.contains(" - ")) {
            try {
                String[] parts = jenisLayananParam.split(" - ", 2);
                namaLayanan = parts[0];
                hargaPerSatuan = Double.parseDouble(parts[1]);
            } catch (NumberFormatException ignored) {}
        } else if (jenisLayananParam != null) {
            try {
                hargaPerSatuan = Double.parseDouble(jenisLayananParam);
            } catch (NumberFormatException ignored) {}
        }

        existing.setNamaLengkap(order.getNamaLengkap());
        existing.setNoWa(order.getNoWa());
        existing.setAlamat(order.getAlamat());
        existing.setJenisLayanan(namaLayanan);
        existing.setBerat(order.getBerat());
        existing.setJumlahItem(order.getJumlahItem());
        existing.setAntarJemput(antarJemput);
        if (order.getTanggalJemput() != null) {
            existing.setTanggalJemput(order.getTanggalJemput());
        }
        existing.setMetodePembayaran(order.getMetodePembayaran());
        existing.setStatus(order.getStatus());
        existing.setCatatan(order.getCatatan());

        if (totalHargaInput > 0) {
            existing.setTotalHarga(totalHargaInput);
        } else if (hargaSatuan > 0) {
            double subtotal = antarJemput;
            if (order.getJumlahItem() != null && order.getJumlahItem() > 0) {
                subtotal += hargaSatuan * order.getJumlahItem();
            } else if (order.getBerat() != null && order.getBerat() > 0) {
                subtotal += hargaSatuan * order.getBerat();
            }
            existing.setTotalHarga(subtotal);
        } else if (hargaPerSatuan > 0) {
            double subtotal = antarJemput;
            if (order.getJumlahItem() != null && order.getJumlahItem() > 0) {
                subtotal += hargaPerSatuan * order.getJumlahItem();
            } else if (order.getBerat() != null && order.getBerat() > 0) {
                subtotal += hargaPerSatuan * order.getBerat();
            }
            existing.setTotalHarga(subtotal);
        }

        orderRepository.saveAndFlush(existing);
        return "redirect:/admin";
    }

    // --- KONFIRMASI PESANAN ---
    @GetMapping("/konfirmasi-pesanan")
    public String konfirmasiPesanan(Model model) {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("orders", orders);
        return "Admin/konfirmasi-pesanan";
    }

    @PostMapping("/konfirmasi-pesanan/{id}/terima")
    public String terimaPesanan(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));
        order.setStatus("Sudah Diterima");
        orderRepository.save(order);
        return "redirect:/admin/konfirmasi-pesanan";
    }

    @PostMapping("/konfirmasi-pesanan/{id}/tolak")
    public String tolakPesanan(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));
        order.setStatus("Ditolak");
        orderRepository.save(order);
        return "redirect:/admin/konfirmasi-pesanan";
    }

    // --- KONFIRMASI PEMBAYARAN ---
    @GetMapping("/konfirmasi-pembayaran")
    public String konfirmasiPembayaran(Model model) {
        List<Order> payments = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("orders", payments);
        return "Admin/konfirmasi-pembayaran";
    }

    @PostMapping("/konfirmasi-pembayaran/{id}/lunas")
    public String lunasPembayaran(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));
        order.setStatus("Sedang Dicuci");
        order.setMetodePembayaran("Lunas");
        orderRepository.save(order);
        return "redirect:/admin/konfirmasi-pembayaran";
    }

    // --- UPDATE STATUS (DARI DROPDOWN) ---
    @PostMapping("/update-status/{id}")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin";
    }

    // --- HAPUS PESANAN ---
    @GetMapping("/hapus/{id}")
    public String hapusOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/admin";
    }
}