package com.company;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;

public class Client {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Scanner scanner;
    private Map<String, Reserva> reservas;


    public Client() throws IOException {
        this.socket= new Socket("localhost", 12345);
        this.output= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.input= new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.scanner= new Scanner(System.in);
        this.reservas= new HashMap<>();
    }

    public void run() throws IOException {
        menuInicial();
        this.socket.close();
    }

    public void serialize(List<String> lista, int opcao) throws IOException {
        this.output.writeInt(opcao);
        for(String l: lista) this.output.writeUTF(l);
    }

    public void autenticar() throws IOException {
        System.out.println("Insira o seu username:");
        String username= getLine();
        if(username.equals("")) return;
        System.out.println("Insira a sua palavra-passe:");
        String pass= getLine();

        this.output.writeInt(0);
        this.output.writeUTF(username);
        this.output.writeUTF(pass);
        this.output.flush();

        boolean resultado= this.input.readBoolean();
        if(!resultado) System.out.println("Autenticação mal sucedida");
        else{
            System.out.println("Autenticação bem sucedida! Bem vindo " + username);
            menuPrincipal();
        }
    }


    public void registar() throws IOException {
        boolean resposta = false;
        while(!resposta){
            System.out.println("Insira um username");
            String username = scanner.nextLine();
            String password1= "1";
            String password2= "2";
            if(username.equals("")) return;

            boolean flag = false;
            while(!password1.equals(password2)){
                if(flag) System.out.println("As passwords têm de ser iguais!");
                flag = true;
                System.out.println("Insira uma password");
                password1 = scanner.nextLine();
                if(password1.equals("")) return;
                System.out.println("Insira novamente a password");
                password2 = scanner.nextLine();
                if(password2.equals("")) return;
            }

            output.writeInt(1);
            output.writeUTF(username);
            output.writeUTF(password1);
            output.flush();

            resposta = input.readBoolean();

            if(!resposta) System.out.println("Username já existente!");
        }
        System.out.println("Registo feito com sucesso");
        System.out.println("Bem-vindo!");
        // menuPrincipal();
    }

    private void reservarVoo() throws IOException {
        int opcao = -1;
        String origem = null;
        String destino = null;
        boolean flag = false;
        List<String> origens = new ArrayList<>();
        List<String> destinos = new ArrayList<>();
        List<List<String>> voos = new ArrayList<>();

        // Pedir origens
        output.writeInt(3);
        output.flush();

        // Receber origens
        int origTam = input.readInt();
        for (int i = 0; i < origTam; i++)
            origens.add(input.readUTF());

        // Escolher origem
        while (opcao <= 0 || opcao > origens.size()) {
            if (flag) System.out.println("Insira um número entre 0 e " + origens.size());
            flag = true;
            System.out.println("Escolha o local de origem");
            printList(origens);
            System.out.println("0 -> Sair");
            System.out.print("Opção: ");
            opcao = getInt();
            if (opcao == 0) {
                output.writeBoolean(false);
                output.flush();
                return;
            }
        }
        origem = origens.get(opcao - 1);

        // Pedir destinos
        output.writeBoolean(true);
        output.writeUTF(origem);
        output.flush();

        // Receber destinos
        int destTam = input.readInt();
        for (int i = 0; i < destTam; i++)
            destinos.add(input.readUTF());

        // Escolher destino
        flag = false;
        opcao = -1;
        while (opcao <= 0 || opcao > destinos.size()) {
            if (flag) System.out.println("Insira um número entre 0 e " + destinos.size());
            flag = true;
            System.out.println("Escolha o local de destino");
            printList(destinos);
            System.out.println("0 -> Sair");
            System.out.print("Opção: ");
            opcao = getInt();
            if (opcao == 0) {
                output.writeBoolean(false);
                output.flush();
                return;
            }
        }
        destino = destinos.get(opcao - 1);

        // Pedir voos
        output.writeBoolean(true);
        output.writeUTF(destino);
        output.flush();

        System.out.println("Sai");

        // Receber voos
        int voosTam = input.readInt();
        for (int i = 0; i < voosTam; i++) {
            int escalaTam = input.readInt();
            voos.add(new ArrayList<>());
            for (int j = 0; j < escalaTam; j++)
                voos.get(i).add(input.readUTF());
        }

        // Escolher voo
        boolean resposta = false;
        int dia= -1, mes= -1, ano= -1;
            flag = false;
            opcao = -1;
            while (opcao <= 0 || opcao > voos.size()) {
                if (flag) System.out.println("Insira um número entre 0 e " + voos.size());
                flag = true;
                System.out.println("Escolha um voo");
                printListOfList(voos);
                System.out.println("0 -> Sair");
                System.out.print("Opção: ");
                opcao = getInt();
                if (opcao == 0) {
                    output.writeBoolean(false);
                    output.flush();
                    return;
                }
                // Tirar data
                System.out.print("Insira o ano: ");
                ano = getInt();
                if (ano == 0) {
                    output.writeBoolean(false);
                    output.flush();
                    return;
                }
                System.out.print(" Insira o mes: ");
                mes = getInt();
                if (mes == 0) {
                    output.writeBoolean(false);
                    output.flush();
                    return;
                }
                System.out.print(" Insira o dia: ");
                dia = getInt();
                if (dia == 0) {
                    output.writeBoolean(false);
                    output.flush();
                    return;
                }
                if (!(ano == (LocalDate.now().getYear()) && mes >= (LocalDate.now().getMonthValue())
                        && mes <= 12 && dia >= (LocalDate.now().getDayOfMonth()) && dia <= 31 ||
                        ano > (LocalDate.now().getYear()) && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31)) {
                    System.out.println("Data Inválida!\nInsira novamente");
                    opcao = -1;
                }
            }

                // Pedir Reserva
                output.writeBoolean(true);
                output.writeInt(opcao - 1);
                output.writeInt(ano);
                output.writeInt(mes);
                output.writeInt(dia);
                output.flush();

                resposta = input.readBoolean();

                if (!resposta) System.out.println("Voo indisponivel!");
                else {
                    System.out.println("Reserva efetuada\nCódigo da reserva: " + input.readUTF());
                }
        }


    private void printList(List<String> l){
        int count = 1;
        for(String s : l)
            System.out.println(count++ + " -> " + s);
    }

    private void printListOfList(List<List<String>> list){
        int count = 1;
        for(List<String> l : list){
            System.out.print(count++);
            for(String s : l) System.out.print(" -> " + s);
            System.out.println("");
        }
    }

    private void menuInicial() throws IOException {
        int opcao = 1;
        while(opcao != 0){
            System.out.println("<---- Bem-Vindo ao AirSD ---->");
            System.out.println("1 -> Autenticar");
            System.out.println("2 -> Registar");
            System.out.println("0 -> Sair");

            System.out.print("Opção: ");
            opcao = getInt();
            switch(opcao){
                case 0:
                    System.out.println("Até breve...");
                    return;
                case 1:
                    autenticar();
                    break;
                case 2:
                    registar();
                    break;
                default:
                    System.out.println("Opção Inválida");
                    break;
            }
        }
    }

    private void menuPrincipal() throws IOException {
        int opcao = 1;
        while(opcao != 0){
            System.out.println("<---- Menu Principal ---->");
            System.out.println("1 -> Reservar Voo");
            System.out.println("2 -> Cancelar Voo");
            System.out.println("3 -> Listar Voos");
            System.out.println("0 -> Sair");

            System.out.print("Opção: ");
            opcao = getInt();
            switch(opcao){
                case 0:
                    return;
                case 1:
                    reservarVoo();
                    break;
                case 2:
                    cancelarVoo();
                    break;
                case 3:
                    listarVoos();
                    break;
                default:
                    System.out.println("Opção Inválida");
                    break;
            }
        }
    }



    private String getLine(){
        return this.scanner.nextLine();
    }

    private int getInt(){
        try {
            int num = this.scanner.nextInt();
            this.scanner.nextLine();
            return num;
        }catch (Exception e) {
            this.scanner.nextLine();
            return -1;
        }
    }

    private float getFloat(){
        try {
            float num = this.scanner.nextFloat();
            this.scanner.nextLine();
            return num;
        }catch (Exception e) {
            this.scanner.nextLine();
            return -1;
        }
    }

    private long getLong(){
        try {
            long num = this.scanner.nextLong();
            this.scanner.nextLine();
            return num;
        }catch (Exception e) {
            this.scanner.nextLine();
            return -1;
        }
    }


    private void listarVoos() throws IOException{
        this.output.writeInt(6);
        this.output.flush();

        int tam = this.input.readInt();

        for(int i=0; i< tam; i++){
            System.out.println(" Origem: " + this.input.readUTF() + " Destino: " + this.input.readUTF() + " Capacidade máxima: " + this.input.readInt());
        }
    }


    public void cancelarVoo() throws IOException {
        this.output.writeInt(7);
        this.output.flush();
        int tamanho = this.input.readInt();

        if(tamanho== 0){
            System.out.println("Lista de reservas vazia.");
            return;
        }

        List<String> listaReservas= new ArrayList<>();
        System.out.println("Tamanho: " + tamanho);
        int opcao = -1;
            for (int i = 0; i < tamanho; i++) {
                String s= new String("Código de reserva: " + this.input.readUTF() + ";");
                int tamanhoEscala= this.input.readInt();
                for(int j= 0; j< tamanhoEscala; j++) {
                    if (j > 0) s += " -> ";

                    s+= (this.input.readUTF());
                }
                s+= " -> " + this.input.readUTF() + " Data: " + this.input.readInt() + "/" + this.input.readInt() + "/" + this.input.readInt();

                listaReservas.add(s);
            }
            while(opcao< 0 || opcao> tamanho) {
                System.out.println(" 0 -> Sair");
                System.out.println("Da sua lista de reservas, selecione a reserva que pretende cancelar: ");
                printList(listaReservas);
                opcao = getInt();
                if (opcao == 0) {
                    this.output.writeInt(-1);
                    this.output.flush();
                    return;
                }
                if (opcao < 0 || opcao > tamanho) {
                    System.out.println("Opção inválida, selecione outra vez!");
                }
            }
        this.output.writeInt(opcao - 1);
        output.flush();

        System.out.println("Reserva cancelada:\n" + listaReservas.get(opcao- 1));
    }


    public static void main(String[] args) throws IOException {
        Client s= new Client();
        s.run();
    }




}
