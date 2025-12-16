package com.isec.das.project2.config;

import com.isec.das.project2.model.*;
import com.isec.das.project2.repository.*;
import com.isec.das.project2.util.EstadoEmprestimo;
import com.isec.das.project2.util.EstadoRegisto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(
            LivroRepository livroRepository,
            PessoaRepository pessoaRepository,
            BibliotecaRepository bibliotecaRepository,
            CopiaLivroRepository copiaLivroRepository,
            RegistoRepository registoRepository,
            EmprestimoRepository emprestimoRepository) {

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

            // Registos
            Registo r1 = registoRepository.save(Registo.builder().pessoa(p1).biblioteca(b1).dataRegisto(LocalDate.now()).estado(EstadoRegisto.ATIVO).build());

            // Cópias
            CopiaLivro c1 = copiaLivroRepository.save(CopiaLivro.builder().livro(l1).biblioteca(b1).build());
            copiaLivroRepository.save(CopiaLivro.builder().livro(l1).biblioteca(b2).build());
            copiaLivroRepository.save(CopiaLivro.builder().livro(l2).biblioteca(b1).build());
            copiaLivroRepository.save(CopiaLivro.builder().livro(l3).biblioteca(b2).build());

            //Emprestimos
            Emprestimo e1 = emprestimoRepository.save(Emprestimo.builder().pessoa(p1).copiaLivro(c1).estado(EstadoEmprestimo.ATIVO).dataEmprestimo(LocalDate.now()).build());


            System.out.println("Dados iniciais carregados com sucesso!");
        };
    }
}
