package TrabalhoPMD.modelo;

import java.util.Map;

public class Pergunta {

    private final String enunciado;
    private final Map<Character, String> alternativas;
    private final char respostaCorreta;
    private final int recompensa;

    public Pergunta(String enunciado,Map<Character, String> alternativas,char respostaCorreta,int recompensa) {

        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
        this.recompensa = recompensa;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public Map<Character, String> getAlternativas() {
        return alternativas;
    }

    public char getRespostaCorreta() {
        return respostaCorreta;
    }

    public int getRecompensa() {
        return recompensa;
    }
}