package com.company;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BaseDados implements Serializable {
    private Map<String, String> clientes;
    private Map<String, String> administradores;
    private List<LocalDate> cancelados;
    //Origem, Destino
    private Map<Map.Entry<String, String>, Voo> listaVoos;
    private Map<String, List<Reserva>> reservas;
    private Lock registosLock= new ReentrantLock();
    private Lock voosLock= new ReentrantLock();


    public BaseDados(){
        this.clientes= new ConcurrentHashMap<>();
        this.administradores= new ConcurrentHashMap<>();
        this.reservas= new ConcurrentHashMap<>();
        this.cancelados= new ArrayList<>();
        this.listaVoos= new ConcurrentHashMap<>();
        this.administradores.put("duarte", "1234567");
    }


    public Map<String, String> getClientes() {
        return clientes;
    }

    public void setClientes(Map<String, String> clientes) {
        this.clientes = clientes;
    }

    public Map<String, String> getAdministradores() {
        return administradores;
    }

    public void setAdministradores(Map<String, String> administradores) {
        this.administradores = administradores;
    }

    public List<LocalDate> getCancelados() {
        return cancelados;
    }

    public void setCancelados(List<LocalDate> cancelados) {
        this.cancelados = cancelados;
    }

    public Map<String, List<Reserva>> getReservas() {
        return reservas;
    }

    public void setReservas(Map<String, List<Reserva>> reservas) {
        this.reservas = reservas;
    }

    public Map<Map.Entry<String, String>, Voo> getListaVoos() {
        return listaVoos;
    }

    public void setListaVoos(Map<Map.Entry<String, String>, Voo> listaVoos) {
        this.listaVoos = listaVoos;
    }
}
