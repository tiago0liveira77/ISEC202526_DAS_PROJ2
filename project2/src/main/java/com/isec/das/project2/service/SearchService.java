package com.isec.das.project2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isec.das.project2.model.FullText;
import com.isec.das.project2.model.Operation;
import com.isec.das.project2.repository.FullTextRepository;
import com.isec.das.project2.repository.LivroRepository;
import com.isec.das.project2.repository.OperationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.isec.das.project2.model.Livro;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Async
    public void executarPesquisaAsync(Long operationId,
                                      String word,
                                      LivroRepository livroRepository,
                                      FullTextRepository fullTextRepository,
                                      OperationRepository operationRepository,
                                      ObjectMapper mapper) {

        try {
            Thread.sleep(10000);

            String palavra = word.toLowerCase();
            List<String> encontrados = new ArrayList<>();

            for (Livro livro : livroRepository.findAll()) {
                FullText fulltext = fullTextRepository.findById(livro.getId()).orElse(null);

                if (fulltext == null || fulltext.getTexto() == null) {
                    continue;
                }

                if (fulltext.getTexto().toLowerCase().contains(palavra)) {
                    encontrados.add(livro.getTitulo());
                }
            }





            Operation op = operationRepository.findById(operationId).orElseThrow();
            op.setResult(mapper.writeValueAsString(encontrados));
            op.setDone(true);

            operationRepository.save(op);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
