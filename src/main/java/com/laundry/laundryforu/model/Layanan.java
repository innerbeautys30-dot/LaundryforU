package com.laundry.laundryforu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "layanan")
public class Layanan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_layanan")
    private String namaLayanan;

    private int harga;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    // --- CONSTRUCTOR ---
    public Layanan() {}

    public Layanan(String namaLayanan, int harga, String deskripsi) {
        this.namaLayanan = namaLayanan;
        this.harga = harga;
        this.deskripsi = deskripsi;
    }

    // --- GETTER & SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNamaLayanan() { return namaLayanan; }
    public void setNamaLayanan(String namaLayanan) { this.namaLayanan = namaLayanan; }

    public int getHarga() { return harga; }
    public void setHarga(int harga) { this.harga = harga; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}