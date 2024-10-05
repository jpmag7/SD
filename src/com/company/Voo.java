package com.company;

import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Voo{
    private String origem;
    private String destino;
    private int capacidadeMax;
    private Map<LocalDate, Integer> lotacao;




    /**
     * Construtor parametrizado
     */
    public Voo(String origem, String destino, int capacidadeMax) {
        this.origem = origem;
        this.destino = destino;
        this.capacidadeMax= capacidadeMax;
        this.lotacao = new ConcurrentHashMap<>();
    }


    public void serialize(DataOutputStream output) throws IOException {
        output.writeUTF(origem);
        output.writeUTF(destino);
        output.writeInt(capacidadeMax);
    }

    public String getOrigem() {
        return origem;
    }

    public String getDestino() {
        return destino;
    }

    public Map<LocalDate, Integer> getCapacidade() {
        return lotacao;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setCapacidade(Map<LocalDate, Integer> capacidade) {
        this.lotacao = capacidade;
    }

    public Map<LocalDate, Integer> getLotacao() {
        return lotacao;
    }

    public int getCapacidadeMax() {
        return capacidadeMax;
    }

    public void setCapacidadeMax(int capacidadeMax) {
        this.capacidadeMax = capacidadeMax;
    }

    public void setLotacao(Map<LocalDate, Integer> lotacao) {
        this.lotacao = lotacao;
    }
}
