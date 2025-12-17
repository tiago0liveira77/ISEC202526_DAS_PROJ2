package com.isec.das.project2.util;

import com.isec.das.project2.model.*;

import java.util.*;

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
            Set<String> nestedFields = new HashSet<>();

            for (String field : fields) {
                if (field.contains(".")) {
                    nestedFields.add(field);
                }
            }


            /*
             * Mapa que associa o nome do objeto raiz aos seus respetivos subcampos.
             * Exemplo:
             *   {
             *     "livro"       -> ["titulo", "autor"],
             *     "biblioteca"  -> ["nome", "localizacao"]
             *   }
             */
            Map<String, Set<String>> nestedFieldMasks = new LinkedHashMap<>();

            // Processa cada field mask complexo
            for(String field : nestedFields){
                int dotIndex = field.indexOf("."); //exemplo: livro.titulo
                if(dotIndex > 0){
                    // Nome do objeto raiz (ex: "livro" ou "biblioteca")
                    String root = field.substring(0, dotIndex);
                    // Nome do atributo interno do objeto (ex: "titulo", "localizacao")
                    String sub = field.substring(dotIndex + 1);

                    // Verifica se ja existe key corresponde ao objeto
                    if (!nestedFieldMasks.containsKey(root)) {
                        nestedFieldMasks.put(root, new LinkedHashSet<>());
                    }
                    //Associa os fields a esse objeto
                    nestedFieldMasks.get(root).add(sub);
                }
            }

            if(nestedFieldMasks.containsKey("livro") && copiaLivro.getLivro() != null){
                Set<String> copiaLivroFields = nestedFieldMasks.get("livro");
                Map<String, Object> livroMap = aplicarFieldMask(copiaLivro.getLivro(), copiaLivroFields);

                map.put("livro", livroMap);
            }

            if(nestedFieldMasks.containsKey("biblioteca") && copiaLivro.getBiblioteca() != null){
                Set<String> bibliotecaFields = nestedFieldMasks.get("biblioteca");
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
            Set<String> nestedFields = new HashSet<>();

            for (String field : fields) {
                if (field.contains(".")) {
                    nestedFields.add(field);
                }
            }

            /*
             * Mapa que associa o nome do objeto raiz aos seus respetivos subcampos.
             * Exemplo:
             *   {
             *     "copiaLivro" -> ["titulo", "autor"],
             *     "pessoa"     -> ["nome", "email"]
             *   }
             */
            Map<String, Set<String>> nestedFieldMasks = new LinkedHashMap<>();

            // Processa cada field mask complexo
            for(String field : nestedFields){
                int dotIndex = field.indexOf("."); //exemplo pessoa.nome
                if(dotIndex > 0){
                    // Nome do objeto raiz (ex: "copiaLivro" ou "pessoa")
                    String root = field.substring(0, dotIndex);
                    // Nome do atributo interno do objeto (ex: "titulo", "nome")
                    String sub = field.substring(dotIndex + 1);

                    // Verifica se ja existe key corresponde ao objeto
                    if (!nestedFieldMasks.containsKey(root)) {
                        nestedFieldMasks.put(root, new LinkedHashSet<>());
                    }
                    //Associa os fields a esse objeto
                    nestedFieldMasks.get(root).add(sub);
                }
            }

            /*
             * 3) APLICAÇÃO DOS FIELD MASKS AOS OBJETOS RELACIONADOS
             * ----------------------------------------------------
             * Para cada objeto relacionado presente no Emprestimo,
             * aplicamos recursivamente o respetivo field mask.
             */

            if(nestedFieldMasks.containsKey("copiaLivro") && emprestimo.getCopiaLivro() != null){
                // Subcampos pedidos para CopiaLivro
                Set<String> copiaLivroFields = nestedFieldMasks.get("copiaLivro");

                // Aplicação recursiva do field mask ao objeto CopiaLivro
                Map<String, Object> copiaLivroMap = aplicarFieldMask(emprestimo.getCopiaLivro(), copiaLivroFields);

                map.put("copiaLivro", copiaLivroMap);
            }

            if(nestedFieldMasks.containsKey("pessoa") && emprestimo.getPessoa() != null){
                // Subcampos pedidos para Pessoa
                Set<String> copiaLivroFields = nestedFieldMasks.get("pessoa");

                // Aplicação recursiva do field mask ao objeto Pessoa
                Map<String, Object> pessoaMap = aplicarFieldMask(emprestimo.getPessoa(), copiaLivroFields);

                map.put("pessoa", pessoaMap);
            }

        }

        return map;
    }

    /**
     *
     * @param registo
     * @param fields
     * @return
     */
    public static Map<String, Object> aplicarFieldMask(Registo registo, Set<String> fields){
        System.out.println(fields);

        /*
         * 1) PROCESSAMENTO DE FIELD MASKS SIMPLES (NÍVEL 1)
         * -----------------------------------------------
         * Aqui tratamos apenas os campos diretos do objeto Emprestimo,
         * como "id", "pessoa", "biblioteca", "estado", etc.
         */
        Map<String, Object> map = new HashMap<>();
        boolean all = fields.contains("*");

        if (all || fields.contains("id")) {
            map.put("id", registo.getId());
        }
        if (all || fields.contains("pessoa")) {
            map.put("pessoa", registo.getPessoa());
        }
        if (all || fields.contains("biblioteca")) {
            map.put("biblioteca", registo.getBiblioteca());
        }
        if (all || fields.contains("estado")) {
            map.put("estado", registo.getEstado());
        }
        if (all || fields.contains("dataRegisto")) {
            map.put("dataRegisto", registo.getDataRegisto());
        }


        /*
         * 2) PROCESSAMENTO DE FIELD MASKS COMPLEXOS (NESTED)
         * -------------------------------------------------
         * Nesta fase tratamos campos com navegação para objetos relacionados,
         * por exemplo:
         *   - "biblioteca.nome"
         *   - "pessoa.nome"
         *
         * A ideia é:
         *   - agrupar os subcampos por objeto raiz
         *   - aplicar o field mask correspondente a cada objeto
         */
        boolean hasNested = fields.stream().anyMatch(f -> f.contains("."));
        if(hasNested){
            // Filtra apenas os field masks que possuem navegação (contêm '.')
            Set<String> nestedFields = new HashSet<>();

            for (String field : fields) {
                if (field.contains(".")) {
                    nestedFields.add(field);
                }
            }

            /*
             * Mapa que associa o nome do objeto raiz aos seus respetivos subcampos.
             * Exemplo:
             *   {
             *     "biblioteca" -> ["nome", "localizacao"],
             *     "pessoa"     -> ["nome", "email"]
             *   }
             */
            Map<String, Set<String>> nestedFieldMasks = new LinkedHashMap<>();

            // Processa cada field mask complexo
            for(String field : nestedFields){
                int dotIndex = field.indexOf("."); //exemplo pessoa.nome
                if(dotIndex > 0){
                    // Nome do objeto raiz (ex: "biblioteca" ou "pessoa")
                    String root = field.substring(0, dotIndex);
                    // Nome do atributo interno do objeto (ex: "localizacao", "nome")
                    String sub = field.substring(dotIndex + 1);

                    // Verifica se ja existe key corresponde ao objeto
                    if (!nestedFieldMasks.containsKey(root)) {
                        nestedFieldMasks.put(root, new LinkedHashSet<>());
                    }
                    //Associa os fields a esse objeto
                    nestedFieldMasks.get(root).add(sub);
                }
            }

            /*
             * 3) APLICAÇÃO DOS FIELD MASKS AOS OBJETOS RELACIONADOS
             * ----------------------------------------------------
             * Para cada objeto relacionado presente no Emprestimo,
             * aplicamos recursivamente o respetivo field mask.
             */

            if(nestedFieldMasks.containsKey("biblioteca") && registo.getBiblioteca() != null){
                // Subcampos pedidos para CopiaLivro
                Set<String> bibliotecaFields = nestedFieldMasks.get("biblioteca");

                // Aplicação recursiva do field mask ao objeto CopiaLivro
                Map<String, Object> bibliotecaMap = aplicarFieldMask(registo.getBiblioteca(), bibliotecaFields);

                map.put("biblioteca", bibliotecaMap);
            }

            if(nestedFieldMasks.containsKey("pessoa") && registo.getPessoa() != null){
                // Subcampos pedidos para Pessoa
                Set<String> pessoaFields = nestedFieldMasks.get("pessoa");

                // Aplicação recursiva do field mask ao objeto Pessoa
                Map<String, Object> pessoaMap = aplicarFieldMask(registo.getPessoa(), pessoaFields);

                map.put("pessoa", pessoaMap);
            }

        }

        return map;
    }
}
