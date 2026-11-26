package TrabalhoPMD.servico;

import TrabalhoPMD.modelo.Pergunta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServicoQuestionario {

    private final List<Pergunta> perguntas;

    public ServicoQuestionario(List<Pergunta> perguntas) {

        this.perguntas = new ArrayList<>(perguntas);
    }

    // Sorteia uma quantidade de perguntas
    public List<Pergunta> sortearPerguntas(int quantidade) {
        
        List<Pergunta> sorteadas = new ArrayList<>(perguntas);
        
        Collections.shuffle(sorteadas);

        int limite = Math.min(quantidade, sorteadas.size());

        return new ArrayList<>(sorteadas.subList(0, limite));
    }

    // Verifica a resposta e retorna a recompensa
    public int verificarResposta(Pergunta pergunta, char resposta) {
        
        if (Character.toUpperCase(resposta) == pergunta.getRespostaCorreta()) {
            return pergunta.getRecompensa();
        }
        return 0;
    }
}
