package com.company;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerWorker implements Runnable {
    private Socket socket;
    private BaseDados dados;
    private DataInputStream input;
    private DataOutputStream output;
    private Scanner scanner;
    private Lock lock;
    private String username;
    private boolean admin;

    public ServerWorker(Socket socket, BaseDados dados, ReentrantLock lock) throws IOException {
        this.socket = socket;
        this.dados= dados;
        this.output= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.input= new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.scanner= new Scanner(System.in);
        this.lock= lock;
    }


    @Override
    public void run() {
        try {
            while (!this.socket.isClosed()) {
                int opcao= this.input.readInt();

                if(opcao== 0) verificaAutenticacaoCliente();

                else if(opcao== 1) registarCliente();

                else if(opcao== 2) verificaAutenticacaoAdmin();

                else if(opcao== 3) reservarVoo();

                else if(opcao== 4) encerramentoDia();

                else if(opcao== 5) inserirVoo();

                else if(opcao== 6) listarVoos();

                else if(opcao== 7) cancelarVoo();

            }

        } catch (Exception e) {
            if(this.admin) System.out.println("Administrador " + this.username + " encerrou a conexão.");
            else System.out.println("Cliente " + this.username + " encerrou a conexão.");
        }

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void verificaAutenticacaoCliente() throws IOException {
        String nome= this.input.readUTF();
        String pass= this.input.readUTF();

        String palavraPasse= null;


        if(this.dados.getClientes().containsKey(nome)) palavraPasse= this.dados.getClientes().get(nome);
        else {
            this.output.writeBoolean(false);
            this.output.flush();
        }

        if(pass.equals(palavraPasse)){
            this.username= nome;
            this.admin= false;
            this.output.writeBoolean(true);
            this.output.flush();
            System.out.println("Cliente " + this.username + " autenticou-se.");
        }
        else this.output.writeBoolean(false);
        this.output.flush();
    }

    public void verificaAutenticacaoAdmin() throws IOException {
        String nome= this.input.readUTF();
        String pass= this.input.readUTF();

        String palavraPasse= null;
        if(this.dados.getAdministradores().containsKey(nome)) palavraPasse= this.dados.getAdministradores().get(nome);
        else {
            this.output.writeBoolean(false);
            this.output.flush();
            return;
        }

        if(pass.equals(palavraPasse)){
            this.username= nome;
            this.admin= true;
            this.output.writeBoolean(true);
            System.out.println("Administrador " + nome + " autenticou-se.");
        }
        else this.output.writeBoolean(false);
        this.output.flush();
    }

    private void registarCliente() throws IOException {
        String username = input.readUTF();
        String password = input.readUTF();

        this.lock.lock();
        if(dados.getClientes().containsKey(username)){
            this.lock.unlock();
            output.writeBoolean(false);
            output.flush();
        }
        else {
            this.dados.getClientes().put(username, password);
            this.lock.unlock();
            this.dados.getReservas().put(username, new ArrayList<Reserva>());
            output.writeBoolean(true);
            output.flush();
            System.out.println(username + " registou-se como cliente.");

        }
    }

    private void encerramentoDia() throws IOException {
        int ano = input.readInt();
        int mes = input.readInt();
        int dia = input.readInt();

        LocalDate data = LocalDate.of(ano, mes, dia);


        if(this.dados.getCancelados().contains(data)) {
            output.writeBoolean(false);
            output.flush();
        } else {
            this.dados.getCancelados().add(data);
            output.writeBoolean(true);
            output.flush();
            System.out.println("As tentativas de reservas de voo do dia " + data + " serão canceladas!");
        }
    }

    private void inserirVoo() throws IOException {
        String origem = input.readUTF();
        String destino = input.readUTF();
        int capacidade = input.readInt();

        Map.Entry<String, String> entry = Map.entry(origem, destino);
        if(this.dados.getListaVoos().containsKey(entry)){
            output.writeBoolean(false);
            output.flush();
        }
        else{
            this.dados.getListaVoos().put(entry, new Voo(origem, destino, capacidade));
            output.writeBoolean(true);
            output.flush();
            System.out.println("Voo " + origem + "-> " + destino + " adicionado pelo administrador: " + this.username);
        }
    }


    private void listarVoos() throws IOException {
        output.writeInt(this.dados.getListaVoos().size());
        for(Voo voo : this.dados.getListaVoos().values()){
            voo.serialize(output);
        }
        output.flush();
    }



    public void cancelarVoo() throws IOException {
        // Envia o tamanho da lista de reservas.
        int tamanho;

        if(!dados.getReservas().containsKey(this.username)) tamanho= 0;
        else tamanho= dados.getReservas().get(this.username).size();

        if(tamanho== 0){
            this.output.writeInt(tamanho);
            this.output.flush();
            return;
        }

        this.output.writeInt(tamanho);
        // Envia os vários voos : -x-y-z Dia 10;
        for(Reserva reserva : dados.getReservas().get(this.username)) {
            this.output.writeUTF(reserva.getCodigo());
            this.output.writeInt(reserva.getEscalas().size());
            for(Voo voo : reserva.getEscalas()) {
               this.output.writeUTF(voo.getOrigem());
            }
            this.output.writeUTF(reserva.getEscalas().get(reserva.getEscalas().size()- 1).getDestino());
            this.output.writeInt(reserva.getDia().getDayOfMonth());
            this.output.writeInt(reserva.getDia().getMonthValue());
            this.output.writeInt(reserva.getDia().getYear());
        }

        this.output.flush();

        int opcao = this.input.readInt();
        if(opcao == -1) {
            System.out.println("O cliente não quis cancelar nenhuma reserva de uma viagem!");
            return;
        }

        Reserva reserva = dados.getReservas().get(this.username).get(opcao);


        for(Voo v: reserva.getEscalas()){
            v.getLotacao().put(reserva.getDia(), v.getLotacao().get(reserva.getDia())- 1);
        }

        dados.getReservas().get(this.username).remove(reserva);



        System.out.println("Reserva " + reserva.getCodigo() + " cancelada pelo cliente: " + this.username);
    }



    private void reservarVoo() throws IOException {
        List<Map.Entry<String, String>> voos = new ArrayList<>(this.dados.getListaVoos().keySet());
        Set<String> origens = new HashSet<>();

        for(Map.Entry<String, String> entry : voos)
            origens.add(entry.getKey());

        // Enviar origens
        output.writeInt(origens.size());
        for(String s : origens)
            output.writeUTF(s);
        output.flush();

        if(!input.readBoolean()) {
            System.out.println("O cliente desistiu da operação");
            return;
        }

        String origem = input.readUTF();

        // Encontrar destinos
        Set<String> destinos = new HashSet<>();
        Set<String> visitados = new HashSet<>();
        encontraDestinos(origem, visitados, destinos, voos);

        // Enviar destinos
        output.writeInt(destinos.size());
        for(String s : destinos)
            output.writeUTF(s);
        output.flush();

        if(!input.readBoolean()) {
            System.out.println("O cliente desistiu da operação");
            return;
        }

        String destino = input.readUTF();

        // Encontrar caminhos
        List<List<String>> caminhos = new ArrayList<>();
        List<String> visitados1 = new ArrayList<>();
        List<String> caminho = new ArrayList<>();

        dfs(voos, origem, destino, visitados1, caminho, caminhos);


        output.writeInt(caminhos.size());
        for(List<String> l : caminhos){
            output.writeInt(l.size());
            for(String s : l)
                output.writeUTF(s);
        }
        this.output.flush();

        if(!input.readBoolean()) {
            System.out.println("O cliente desistiu da operação");
            return;
        }

        List<String> opcao = caminhos.get(input.readInt());
        LocalDate data = LocalDate.of(input.readInt(), input.readInt(), input.readInt());
        System.out.println(data.toString());
        boolean valido = true;

        // Confirmar validade do voo
        for(int i = 0; i < opcao.size() - 1; i++){
            Map.Entry<String, String> entry = Map.entry(opcao.get(i), opcao.get(i + 1));
            Voo voo = this.dados.getListaVoos().get(entry);

            if(!voo.getLotacao().containsKey(data)) voo.getLotacao().put(data, 0);
            if(voo.getLotacao().get(data) >= voo.getCapacidadeMax() ||
                    this.dados.getCancelados().contains(data)){
                valido = false;
                break;
            }
        }

        // Fazer reservas
        String codigo= "";
        if(valido) {
            List<Voo> voosParaReserva = new ArrayList<>();
            for (int i = 0; i < opcao.size() - 1; i++) {
                Map.Entry<String, String> entry = Map.entry(opcao.get(i), opcao.get(i + 1));
                Voo voo = this.dados.getListaVoos().get(entry);
                voo.getLotacao().put(data, voo.getLotacao().get(data) + 1);
                voosParaReserva.add(voo);
                System.out.println("Vagas disponíveis para o voo " + voo.getOrigem() + " -> " + voo.getDestino() + ": " +  (voo.getCapacidadeMax() - voo.getLotacao().get(data)));
            }


            codigo = Arrays.hashCode(new String[]{this.username + System.currentTimeMillis()}) + "";
            Reserva reserva = new Reserva(codigo, voosParaReserva, data);
            if (!this.dados.getReservas().containsKey(this.username)) {
                this.dados.getReservas().put(this.username, new ArrayList<>());
            }
            this.dados.getReservas().get(this.username).add(reserva);
        }

        this.output.writeBoolean(valido);
        if(valido) this.output.writeUTF(codigo);
        this.output.flush();
    }


    private void encontraDestinos(String origem, Set<String> visitados, Set<String> destinos, List<Map.Entry<String, String>> voos){
        visitados.add(origem);
        for(Map.Entry<String, String> entry : voos){
            if(entry.getKey().equals(origem) && !visitados.contains(entry.getValue())){
                destinos.add(entry.getValue());
                encontraDestinos(entry.getValue(), visitados, destinos, voos);
            }
        }
    }


    private void dfs(List<Map.Entry<String, String>> voos, String origem, String d, List<String> visitados, List<String> caminho, List<List<String>> caminhos){
        caminho.add(origem);
        List<String> adjacencia = adjacentes(origem, voos);
        if(origem.equals(d)){
            caminhos.add(new ArrayList<>(caminho));
            caminho.remove(caminho.size() - 1);
            return;
        }else visitados.add(origem);

        for(String s: adjacencia){
            if(!visitados.contains(s)) {
                dfs(voos, s, d, visitados, caminho, caminhos);
            }
        }
        caminho.remove(caminho.size() - 1);
        visitados.remove(visitados.indexOf(origem));
    }

    private List<String> adjacentes(String origem, List<Map.Entry<String, String>> voos){
        List<String> adj = new ArrayList<>();
        for(Map.Entry<String, String> entry : voos)
            if(entry.getKey().equals(origem))
                adj.add(entry.getValue());
        return adj;
    }

}
