package com.literalura.controller;

import com.google.gson.*;
import com.literalura.model.Livro;
import com.literalura.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.net.http.*;
import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @PostMapping("/buscar")
    public Livro buscarLivro(@RequestParam String titulo) {
        try {
            String tituloSanitizado = java.text.Normalizer.normalize(titulo, java.text.Normalizer.Form.NFD)
                .replaceAll("[^\p{ASCII}]", "").replace(" ", "+");
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
                if (autores.size() == 0) return null;

                JsonObject autorJson = autores.get(0).getAsJsonObject();
                String nomeAutor = autorJson.get("name").getAsString();
                Integer nascimento = autorJson.get("birth_year").isJsonNull() ? 0 : autorJson.get("birth_year").getAsInt();
                Integer falecimento = autorJson.get("death_year").isJsonNull() ? 0 : autorJson.get("death_year").getAsInt();

                Livro livro = new Livro();
                livro.setTitulo(nomeLivro);
                livro.setAutor(nomeAutor);
                livro.setNascimento(nascimento);
                livro.setFalecimento(falecimento);

                return livroRepository.save(livro);
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
        return null;
    }

    @GetMapping
    public List<Livro> listarLivros() {
        return livroRepository.findAll();
    }

    @GetMapping("/autor")
    public List<Livro> buscarPorAutor(@RequestParam String nome) {
        return livroRepository.findByAutorContainingIgnoreCase(nome);
    }
}