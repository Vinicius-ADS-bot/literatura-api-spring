package com.literalura.controller;

import com.google.gson.*;
import com.literalura.model.Livro;
import com.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    public void buscarLivro(String titulo) {
        try {
            String tituloSanitizado = java.text.Normalizer.normalize(titulo, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "").replace(" ", "+");
            String url = "https://gutendex.com/books/?search=" + tituloSanitizado;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray resultados = json.getAsJsonArray("results");

            if (resultados.size() > 0) {
                JsonObject livroJson = resultados.get(0).getAsJsonObject();
                String nomeLivro = livroJson.get("title").getAsString();

                JsonArray autores = livroJson.getAsJsonArray("authors");
                String nomeAutor = "Desconhecido";
                Integer nascimento = 0;
                Integer falecimento = 0;

                if (autores.size() > 0) {
                    JsonObject autorJson = autores.get(0).getAsJsonObject();
                    nomeAutor = autorJson.get("name").getAsString();
                    nascimento = autorJson.get("birth_year").isJsonNull() ? 0 : autorJson.get("birth_year").getAsInt();
                    falecimento = autorJson.get("death_year").isJsonNull() ? 0 : autorJson.get("death_year").getAsInt();
                }

                Livro livro = new Livro();
                livro.setTitulo(nomeLivro);
                livro.setAutor(nomeAutor);
                livro.setNascimento(nascimento);
                livro.setFalecimento(falecimento);

                livroRepository.save(livro);
                System.out.println("Livro encontrado e salvo: " + livro.getTitulo() + " por " + livro.getAutor());
            } else {
                System.out.println("Livro não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar livro: " + e.getMessage());
        }
    }

    public void listarLivros() {
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado.");
        } else {
            livros.forEach(livro -> System.out.println("Título: " + livro.getTitulo() + ", Autor: " + livro.getAutor()));
        }
    }

    public void buscarPorAutor(String nome) {
        List<Livro> livros = livroRepository.findByAutorContainingIgnoreCase(nome);
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado para o autor: " + nome);
        } else {
            livros.forEach(livro -> System.out.println("Título: " + livro.getTitulo() + ", Autor: " + livro.getAutor()));
        }
    }

    public void listarAutores() {
        List<String> autores = livroRepository.findAll().stream()
                .map(Livro::getAutor)
                .distinct()
                .collect(Collectors.toList());
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor registrado.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    public void listarAutoresVivosNoAno(int ano) {
        List<Livro> livros = livroRepository.findAll();
        List<String> autoresVivos = livros.stream()
                .filter(livro -> livro.getNascimento() <= ano && (livro.getFalecimento() == 0 || livro.getFalecimento() >= ano))
                .map(Livro::getAutor)
                .distinct()
                .collect(Collectors.toList());

        if (autoresVivos.isEmpty()) {
            System.out.println("Nenhum autor vivo encontrado no ano " + ano + ".");
        } else {
            System.out.println("Autores vivos no ano " + ano + ":");
            autoresVivos.forEach(System.out::println);
        }
    }
}


