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

            List<String> encontrados = livroRepository.findAll().stream()
                    .filter(l -> {
                        FullText ft = fullTextRepository.findById(l.getId()).orElse(null);
                        if (ft == null || ft.getTexto() == null) return false;
                        return ft.getTexto().toLowerCase().contains(word.toLowerCase());
                    })
                    .map(Livro::getTitulo)
                    .collect(Collectors.toList());

            Operation op = operationRepository.findById(operationId).orElseThrow();
            op.setResult(mapper.writeValueAsString(encontrados));
            op.setDone(true);

            operationRepository.save(op);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
