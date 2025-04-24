package models;

public class Veiculo {
    private int id;
    private String modelo;
    private String placa;
    private String ano;
    private String chassi;
    private int clienteId;

 
    public Veiculo(String modelo, String placa, int clienteId) {
        this.modelo = modelo;
        this.placa = placa;
        this.clienteId = clienteId;
    }
    
    public Veiculo(String modelo, String placa, String ano, String chassi, int clienteId) {
        this.modelo = modelo;
        this.placa = placa;
        this.ano = ano;
        this.chassi = chassi;
        this.clienteId = clienteId;
    }
    
    public Veiculo(int id, String modelo, String placa, String ano, String chassi, int clienteId) {
        this.id = id;
        this.modelo = modelo;
        this.placa = placa;
        this.ano = ano;
        this.chassi = chassi;
        this.clienteId = clienteId;
    }

   
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }
    
    public String getAno() {
    	return ano; 
    }
    
    public void setAno(String ano) {
    	this.ano = ano; 
    }
    
    public String getChassi() {
    	return chassi; 
    }
    
    public void setChassi(String chassi) {
    	this.chassi = chassi; 
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
}