package com.isec.das.project2.repo;

import com.isec.das.project2.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FakeDatabase {
    // Maps thread-safe para simular tabelas
    public final Map<String, Library> libraries = new ConcurrentHashMap<>();
    public final Map<String, Person> people = new ConcurrentHashMap<>();
    public final Map<String, Book> books = new ConcurrentHashMap<>();
    public final Map<String, Loan> loans = new ConcurrentHashMap<>();
    public final Map<String, Operation> operations = new ConcurrentHashMap<>();

    private final AtomicInteger opCounter = new AtomicInteger(1);

    @PostConstruct
    public void init() {
        System.out.println("--- Inicializando Fake Database ---");

        // 1. Criar Bibliotecas
        Library lib1 = new Library("lib-1", "Central Library", "Downtown", new ArrayList<>());
        Library lib2 = new Library("lib-2", "Community Center", "Suburbs", new ArrayList<>());
        libraries.put(lib1.getId(), lib1);
        libraries.put(lib2.getId(), lib2);

        // 2. Criar Pessoas
        Person p1 = new Person("p-1", "Alice Smith");
        Person p2 = new Person("p-2", "Bob Jones");
        Person p3 = new Person("p-3", "Charlie Brown");
        people.put(p1.getId(), p1);
        people.put(p2.getId(), p2);
        people.put(p3.getId(), p3);

        // 3. Registar membros (Associação)
        lib1.getMemberIds().add("p-1"); // Alice é membro da Lib 1
        lib1.getMemberIds().add("p-2"); // Bob é membro da Lib 1
        // Charlie não é membro de nenhuma (para testar erros de permissão)

        // 4. Criar Livros (Gerar 25 para testar paginação > 10)
        for (int i = 1; i <= 25; i++) {
            String libId = (i % 2 == 0) ? "lib-2" : "lib-1"; // Distribui entre as bibliotecas
            String author = (i % 3 == 0) ? "J.R.R. Tolkien" : "Isaac Asimov";

            Book b = new Book();
            b.setId("b-" + i);
            b.setTitle("Sci-Fi Adventure Vol. " + i);
            b.setAuthor(author);
            b.setLibraryId(libId);

            // Configurar um texto específico para o LRO Search testar depois
            if (i == 1) {
                b.getOverview().setText("This creates a unique keyword for the LRO search test.");
            }

            books.put(b.getId(), b);
        }

        // 5. Criar um empréstimo ativo inicial
        Loan l1 = new Loan(UUID.randomUUID().toString(), "b-1", "p-1", "lib-1", "2023-12-01", true);
        loans.put(l1.getId(), l1);

        System.out.println("--- DB Inicializada: " + books.size() + " livros criados. ---");
    }

    // Helper para gerar IDs de operações LRO
    public String generateOpId() {
        return "op-" + opCounter.getAndIncrement();
    }
}