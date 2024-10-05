package com.company;

import java.time.LocalDate;
import java.util.List;

public class Reserva {
    private String codigo;
    private List<Voo> escalas;
    private LocalDate dia;

    public Reserva(String codigo, List<Voo> escalas, LocalDate dia) {
        this.codigo = codigo;
        this.escalas = escalas;
        this.dia= dia;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public List<Voo> getEscalas() {
        return escalas;
    }

    public void setEscalas(List<Voo> escalas) {
        this.escalas = escalas;
    }

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }
}
