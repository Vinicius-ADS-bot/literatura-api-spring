package com.literalura;

import com.literalura.controller.LivroController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class LiteraturaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiteraturaApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(LivroController livroController) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            int opcao = -1;

            while (opcao != 0) {
                System.out.println("\nEscolha uma opção:");
                System.out.println("1. Buscar livro por título");
                System.out.println("2. Listar todos os livros registrados");
                System.out.println("3. Buscar livros por autor");
                System.out.println("4. Listar autores registrados");
                System.out.println("5. Listar autores vivos em determinado ano");
                System.out.println("0. Sair");
                System.out.print("Opção: ");

                try {
                    opcao = Integer.parseInt(scanner.nextLine());

                    switch (opcao) {
                        case 1:
                            System.out.print("Digite o título do livro: ");
                            String titulo = scanner.nextLine();
                            livroController.buscarLivro(titulo);
                            break;
                        case 2:
                            livroController.listarLivros();
                            break;
                        case 3:
                            System.out.print("Digite o nome do autor: ");
                            String autor = scanner.nextLine();
                            livroController.buscarPorAutor(autor);
                            break;
                        case 4:
                            livroController.listarAutores();
                            break;
                        case 5:
                            System.out.print("Digite o ano: ");
                            int ano = Integer.parseInt(scanner.nextLine());
                            livroController.listarAutoresVivosNoAno(ano);
                            break;
                        case 0:
                            System.out.println("Saindo...");
                            break;
                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Por favor, digite um número.");
                } catch (Exception e) {
                    System.out.println("Ocorreu um erro: " + e.getMessage());
                }
            }
            scanner.close();
        };
    }
}


