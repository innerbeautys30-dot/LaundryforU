package com.laundry.laundryforu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SummaryController {

    // @GetMapping("/") sudah dihapus dari sini agar tidak bentrok dengan AuthController

    @GetMapping("/layanan")
    public String tampilkanLayanan() {
        return "layanan"; 
    }

    @GetMapping("/harga")
    public String tampilkanHarga() {
        return "harga";
    }

    @GetMapping("/cara-order")
    public String tampilkanCaraOrder() {
        return "cara-order";
    }

    @GetMapping("/tentang-kami")
    public String tampilkanTentangKami() {
        return "tentang-kami";
    }
}