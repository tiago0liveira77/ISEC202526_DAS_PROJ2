package com.isec.das.project2.config;

import com.isec.das.project2.model.Biblioteca;
import com.isec.das.project2.model.CopiaLivro;
import com.isec.das.project2.model.Livro;
import com.isec.das.project2.model.Pessoa;
import com.isec.das.project2.repository.BibliotecaRepository;
import com.isec.das.project2.repository.CopiaLivroRepository;
import com.isec.das.project2.repository.LivroRepository;
import com.isec.das.project2.repository.PessoaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            LivroRepository livroRepository,
            PessoaRepository pessoaRepository,
            BibliotecaRepository bibliotecaRepository,
            CopiaLivroRepository copiaLivroRepository) {

        return args -> {
            System.out.println("A carregar Dados iniciais!");
            // Bibliotecas
            Biblioteca b1 = bibliotecaRepository.save(Biblioteca.builder().nome("Biblioteca Central").localizacao("Coimbra").build());
            Biblioteca b2 = bibliotecaRepository.save(Biblioteca.builder().nome("Biblioteca Norte").localizacao("braga").build());

            // Pessoas
            Pessoa p1 = pessoaRepository.save(Pessoa.builder().nome("João Silva").email("joao@email.com").build());
            Pessoa p2 = pessoaRepository.save(Pessoa.builder().nome("Maria Costa").email("maria@email.com").build());

            // Livros
            Livro l1 = livroRepository.save(Livro.builder().titulo("O Senhor dos Anéis").autor("J.R.R. Tolkien").build());
            Livro l2 = livroRepository.save(Livro.builder().titulo("1984").autor("George Orwell").build());
            Livro l3 = livroRepository.save(Livro.builder().titulo("Dom Quixote").autor("Miguel de Cervantes").build());

            // Cópias
            copiaLivroRepository.save(CopiaLivro.builder().livro(l1).biblioteca(b1).build());
            copiaLivroRepository.save(CopiaLivro.builder().livro(l1).biblioteca(b2).build());
            copiaLivroRepository.save(CopiaLivro.builder().livro(l2).biblioteca(b1).build());
            copiaLivroRepository.save(CopiaLivro.builder().livro(l3).biblioteca(b2).build());

            System.out.println("Dados iniciais carregados com sucesso!");
        };
    }
}
