package com.literalura.model;

import jakarta.persistence.*;

@Entity
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private Integer nascimento;
    private Integer falecimento;

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public Integer getNascimento() { return nascimento; }
    public void setNascimento(Integer nascimento) { this.nascimento = nascimento; }

    public Integer getFalecimento() { return falecimento; }
    public void setFalecimento(Integer falecimento) { this.falecimento = falecimento; }
}