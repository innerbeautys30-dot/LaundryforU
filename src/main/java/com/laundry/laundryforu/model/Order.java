package com.laundry.laundryforu.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_lengkap")
    private String namaLengkap;
    
    @Column(name = "no_wa")
    private String noWa;
    
    private String alamat;
    private String catatan;

    @Column(name = "jenis_layanan")
    private String jenisLayanan;
    
    private Double berat;

    // Tambahkan di dalam class Order (setelah field berat)
@Column(name = "jumlah_item")
private Integer jumlahItem;

// Tambahkan Getter & Setter
public Integer getJumlahItem() { return jumlahItem; }
public void setJumlahItem(Integer jumlahItem) { this.jumlahItem = jumlahItem; }
    
    @Column(name = "tanggal_jemput")
    private LocalDate tanggalJemput;
    
    @Column(name = "metode_pembayaran")
    private String metodePembayaran;
    
    private String status = "PENDING"; 

    // Tambahkan field ini agar bisa menampilkan total di admin
    @Column(name = "total_harga")
    private Double totalHarga;

    @Column(name = "antar_jemput")
    private Double antarJemput;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // --- CONSTRUCTOR ---
    public Order() {}

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getNoWa() { return noWa; }
    public void setNoWa(String noWa) { this.noWa = noWa; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getJenisLayanan() { return jenisLayanan; }
    public void setJenisLayanan(String jenisLayanan) { this.jenisLayanan = jenisLayanan; }

    public Double getBerat() { return berat; }
    public void setBerat(Double berat) { this.berat = berat; }

    public LocalDate getTanggalJemput() { return tanggalJemput; }
    public void setTanggalJemput(LocalDate tanggalJemput) { this.tanggalJemput = tanggalJemput; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(Double totalHarga) { this.totalHarga = totalHarga; }

    public Double getAntarJemput() { return antarJemput; }
    public void setAntarJemput(Double antarJemput) { this.antarJemput = antarJemput; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}