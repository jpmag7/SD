package com.company;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;

public class Administrador {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private Scanner scanner;

    public Administrador() throws IOException {
        this.socket= new Socket("localhost", 12345);
        this.output= new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.input= new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.scanner= new Scanner(System.in);
    }

    public void run() throws IOException {
        menuInicial();
        this.socket.close();
    }

    public void autenticar() throws IOException {
        System.out.println("Insira o seu username:");
        String username= scanner.nextLine();
        if(username.equals("")) return;
        System.out.println("Insira a sua palavra-passe:");
        String pass= scanner.nextLine();

        this.output.writeInt(2);
        this.output.writeUTF(username);
        this.output.writeUTF(pass);
        this.output.flush();

        boolean resultado= this.input.readBoolean();

        if(!resultado) System.out.println("Autenticação mal sucedida");
        else{
            System.out.println("Autenticação bem sucedida");
            menuAdmin();
        }
    }



    private void menuInicial() throws IOException {
        int opcao = 1;
        while(opcao != 0){
            System.out.println("<---- Bem-Vindo Administrador ---->");
            System.out.println("1 -> Autenticar");
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
                default:
                    System.out.println("Opção Inválida");
                    break;
            }
        }
    }

    private void menuAdmin() throws IOException {
        int op = 1;
        while(op != 0){
            System.out.println("<---- Menu Admin ---->");
            System.out.println("1 -> Inserir Voo");
            System.out.println("2 -> Encerrar um dia");
            System.out.println("3 -> Listar voos");
            System.out.println("0 -> Sair");

            System.out.print("Opção: ");
            op = getInt();

            switch(op){
                case 0:
                    System.out.println("Até breve...");
                    return;
                case 1:
                    inserirVoo();
                    break;
                case 2:
                    encerramentoDia();
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

    public void encerramentoDia() throws IOException {
        System.out.println("Indique o dia que pretende encerrar as reservas de voo: ");
        int flag = 0;
        while (flag == 0) {
            System.out.print("Insira o ano: ");
            int ano = getInt();
            System.out.print(" Insira o mes: ");
            int mes = getInt();
            System.out.print(" Insira o dia: ");
            int dia = getInt();
            if (ano == (LocalDate.now().getYear()) && mes >= (LocalDate.now().getMonthValue())
                    && mes <= 12 && dia >= (LocalDate.now().getDayOfMonth()) && dia <= 31 ||
                    ano > (LocalDate.now().getYear()) && mes >= 1 && mes <= 12 && dia >=1 && dia <= 31) {
                this.output.writeInt(4);
                this.output.writeInt(ano);
                this.output.writeInt(mes);
                this.output.writeInt(dia);
                this.output.flush();

                boolean resposta = this.input.readBoolean();
                if (resposta) {
                    System.out.println("Bem sucedido!");
                } else {
                    System.out.println("O dia já se encontra encerrado!");
                }
                flag = 1;
            } else {
                System.out.println("Data inválida, insira novamente os dados!");
            }
        }
    }

    private void inserirVoo() throws IOException {
        System.out.println("Indique a origem:");
        String origem = getLine();
        System.out.println("Indique o Destino:");
        String destino = getLine();
        System.out.println("Indique a Capacidade:");
        int cap = getInt();
        this.output.writeInt(5);
        this.output.writeUTF(origem);
        this.output.writeUTF(destino);
        this.output.writeInt(cap);
        this.output.flush();

        boolean flag= this.input.readBoolean();

        if(flag) System.out.println("Voo: " + origem + " -> " + destino + " foi inserido!");
        else System.out.println("Voo já existe!");
    }

    public static void main(String[] args) throws IOException {
        Administrador admin= new Administrador();
        admin.run();
    }


}
