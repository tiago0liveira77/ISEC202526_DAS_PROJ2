package com.isec.das.project2.util;

import com.isec.das.project2.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class FieldMasks {
    /**
     *
     * @param biblioteca
     * @param fields
     * @return
     */
    public static Map<String, Object> aplicarFieldMask(Biblioteca biblioteca, Set<String> fields) {

        Map<String, Object> map = new HashMap<>();

        boolean all = fields.contains("*");

        if (all || fields.contains("id")) {
            map.put("id", biblioteca.getId());
        }
        if (all || fields.contains("nome")) {
            map.put("nome", biblioteca.getNome());
        }
        if (all || fields.contains("localizacao")) {
            map.put("localizacao", biblioteca.getLocalizacao());
        }

        return map;
    }

    /**
     *
     * @param livro
     * @param fields
     * @return
     */
    public static Map<String, Object> aplicarFieldMask(Livro livro, Set<String> fields) {

        Map<String, Object> map = new HashMap<>();

        boolean all = fields.contains("*");

        if (all || fields.contains("id")) {
            map.put("id", livro.getId());
        }
        if (all || fields.contains("titulo")) {
            map.put("titulo", livro.getTitulo());
        }
        if (all || fields.contains("autor")) {
            map.put("autor", livro.getAutor());
        }

        return map;
    }

    /**
     *
     * @param pessoa
     * @param fields
     * @return
     */
    public static Map<String, Object> aplicarFieldMask(Pessoa pessoa, Set<String> fields) {

        Map<String, Object> map = new HashMap<>();

        boolean all = fields.contains("*");

        if (all || fields.contains("id")) {
            map.put("id", pessoa.getId());
        }
        if (all || fields.contains("nome")) {
            map.put("nome", pessoa.getNome());
        }
        if (all || fields.contains("email")) {
            map.put("email", pessoa.getEmail());
        }

        return map;
    }

    /**
     *
     * @param copiaLivro
     * @param fields
     * @return
     */
    public static Map<String, Object> aplicarFieldMask(CopiaLivro copiaLivro, Set<String> fields) {

        Map<String, Object> map = new HashMap<>();

        /*
         * 1) PROCESSAMENTO DE FIELD MASKS SIMPLES (NÍVEL 1)
         * -----------------------------------------------
         * Aqui tratamos apenas os campos diretos do objeto CopiaLivro,
         * como "id", "livro", "biblioteca".
         */

        boolean all = fields.contains("*");

        if (all || fields.contains("id")) {
            map.put("id", copiaLivro.getId());
        }
        if (all || fields.contains("livro")) {
            map.put("livro", copiaLivro.getLivro());
        }
        if (all || fields.contains("biblioteca")) {
            map.put("biblioteca", copiaLivro.getBiblioteca());
        }

        /*
         * 2) PROCESSAMENTO DE FIELD MASKS COMPLEXOS (NESTED)
         * -------------------------------------------------
         * Nesta fase tratamos campos com navegação para objetos relacionados,
         * por exemplo:
         *   - "livro.titulo"
         *   - "biblioteca.nome"
         *
         * A ideia é:
         *   - agrupar os subcampos por objeto raiz
         *   - aplicar o field mask correspondente a cada objeto
         */
        boolean hasNested = fields.stream().anyMatch(f -> f.contains("."));
        if(hasNested){
            // Filtra apenas os field masks que possuem navegação (contêm '.')
            Set<String> nestedFields = fields.stream()
                    .filter(f -> f.contains("."))
                    .collect(Collectors.toSet());

            /*
             * Mapa que associa o nome do objeto raiz aos seus respetivos subcampos.
             * Exemplo:
             *   {
             *     "livro"       -> ["titulo", "autor"],
             *     "biblioteca"  -> ["nome", "localizacao"]
             *   }
             */
            Map<String, Set<String>> rootToSubFields = new LinkedHashMap<>();

            // Processa cada field mask complexo
            for(String f : nestedFields){
                int dotIndex = f.indexOf(".");
                if(dotIndex > 0){
                    // Nome do objeto raiz (ex: "livro" ou "biblioteca")
                    String root = f.substring(0, dotIndex);
                    // Nome do atributo interno do objeto (ex: "titulo", "localizacao")
                    String sub = f.substring(dotIndex + 1);

                    // Agrupa os subcampos por objeto raiz
                    rootToSubFields.computeIfAbsent(root, k -> new LinkedHashSet<>()).add(sub);
                }
            }

            if(rootToSubFields.containsKey("livro") && copiaLivro.getLivro() != null){
                Set<String> copiaLivroFields = rootToSubFields.get("livro");
                Map<String, Object> livroMap = aplicarFieldMask(copiaLivro.getLivro(), copiaLivroFields);

                map.put("livro", livroMap);
            }

            if(rootToSubFields.containsKey("biblioteca") && copiaLivro.getBiblioteca() != null){
                Set<String> bibliotecaFields = rootToSubFields.get("biblioteca");
                Map<String, Object> livroMap = aplicarFieldMask(copiaLivro.getBiblioteca(), bibliotecaFields);

                map.put("biblioteca", livroMap);
            }

        }

        return map;
    }

    /**
     *
     * @param emprestimo
     * @param fields
     * @return
     */
    public static Map<String, Object> aplicarFieldMask(Emprestimo emprestimo, Set<String> fields){
        System.out.println(fields);

        /*
         * 1) PROCESSAMENTO DE FIELD MASKS SIMPLES (NÍVEL 1)
         * -----------------------------------------------
         * Aqui tratamos apenas os campos diretos do objeto Emprestimo,
         * como "id", "pessoa", "copiaLivro", "estado", etc.
         */
        Map<String, Object> map = new HashMap<>();
        boolean all = fields.contains("*");

        if (all || fields.contains("id")) {
            map.put("id", emprestimo.getId());
        }
        if (all || fields.contains("pessoa")) {
            map.put("pessoa", emprestimo.getPessoa());
        }
        if (all || fields.contains("copiaLivro")) {
            map.put("copiaLivro", emprestimo.getCopiaLivro());
        }
        if (all || fields.contains("estado")) {
            map.put("estado", emprestimo.getEstado());
        }
        if (all || fields.contains("dataEmprestimo")) {
            map.put("dataEmprestimo", emprestimo.getDataEmprestimo());
        }
        if (all || fields.contains("dataDevolucao")) {
            map.put("dataDevolucao", emprestimo.getDataDevolucao());
        }

        /*
         * 2) PROCESSAMENTO DE FIELD MASKS COMPLEXOS (NESTED)
         * -------------------------------------------------
         * Nesta fase tratamos campos com navegação para objetos relacionados,
         * por exemplo:
         *   - "copiaLivro.titulo"
         *   - "pessoa.nome"
         *
         * A ideia é:
         *   - agrupar os subcampos por objeto raiz
         *   - aplicar o field mask correspondente a cada objeto
         */
        boolean hasNested = fields.stream().anyMatch(f -> f.contains("."));
        if(hasNested){
            // Filtra apenas os field masks que possuem navegação (contêm '.')
            Set<String> nestedFields = fields.stream()
                    .filter(f -> f.contains("."))
                    .collect(Collectors.toSet());

            /*
             * Mapa que associa o nome do objeto raiz aos seus respetivos subcampos.
             * Exemplo:
             *   {
             *     "copiaLivro" -> ["titulo", "autor"],
             *     "pessoa"     -> ["nome", "email"]
             *   }
             */
            Map<String, Set<String>> rootToSubFields = new LinkedHashMap<>();

            // Processa cada field mask complexo
            for(String f : nestedFields){
                int dotIndex = f.indexOf(".");
                if(dotIndex > 0){
                    // Nome do objeto raiz (ex: "copiaLivro" ou "pessoa")
                    String root = f.substring(0, dotIndex);
                    // Nome do atributo interno do objeto (ex: "titulo", "nome")
                    String sub = f.substring(dotIndex + 1);

                    // Agrupa os subcampos por objeto raiz
                    rootToSubFields.computeIfAbsent(root, k -> new LinkedHashSet<>()).add(sub);
                }
            }

            /*
             * 3) APLICAÇÃO DOS FIELD MASKS AOS OBJETOS RELACIONADOS
             * ----------------------------------------------------
             * Para cada objeto relacionado presente no Emprestimo,
             * aplicamos recursivamente o respetivo field mask.
             */

            if(rootToSubFields.containsKey("copiaLivro") && emprestimo.getCopiaLivro() != null){
                // Subcampos pedidos para CopiaLivro
                Set<String> copiaLivroFields = rootToSubFields.get("copiaLivro");

                // Aplicação recursiva do field mask ao objeto CopiaLivro
                Map<String, Object> copiaLivroMap = aplicarFieldMask(emprestimo.getCopiaLivro(), copiaLivroFields);

                map.put("copiaLivro", copiaLivroMap);
            }

            if(rootToSubFields.containsKey("pessoa") && emprestimo.getPessoa() != null){
                // Subcampos pedidos para Pessoa
                Set<String> copiaLivroFields = rootToSubFields.get("pessoa");

                // Aplicação recursiva do field mask ao objeto Pessoa
                Map<String, Object> pessoaMap = aplicarFieldMask(emprestimo.getPessoa(), copiaLivroFields);

                map.put("pessoa", pessoaMap);
            }

        }

        return map;
    }
}
