package models;

public class Servico {
    private int id;
    private String descricao;
    private double preco;
    private String data;
    private Integer veiculoId;
    private int quantidade; // New field

    // Original constructor without quantity
    public Servico(int id, String descricao, double preco, String data, Integer veiculoId) {
        this.id = id;
        this.descricao = descricao;
        this.preco = preco;
        this.data = data;
        this.veiculoId = veiculoId;
        this.quantidade = 1; // Default to 1
    }

    // Constructor with quantity
    public Servico(int id, String descricao, double preco, String data, Integer veiculoId, int quantidade) {
        this.id = id;
        this.descricao = descricao;
        this.preco = preco;
        this.data = data;
        this.veiculoId = veiculoId;
        this.quantidade = quantidade;
    }

    // Constructor without ID (used for new services)
    public Servico(String descricao, double preco, String data, Integer veiculoId) {
        this(0, descricao, preco, data, veiculoId, 1);
    }

    // Constructor with quantity for new services
    public Servico(String descricao, double preco, String data, Integer veiculoId, int quantidade) {
        this(0, descricao, preco, data, veiculoId, quantidade);
    }

    // Constructor without data
    public Servico(int id, String descricao, double preco, Integer veiculoId) {
        this(id, descricao, preco, null, veiculoId, 1);
    }

    // Constructor with quantity without data
    public Servico(int id, String descricao, double preco, Integer veiculoId, int quantidade) {
        this(id, descricao, preco, null, veiculoId, quantidade);
    }

    // Constructor with only description and price (for ad-hoc services)
    public Servico(String descricao, double preco) {
        this(0, descricao, preco, null, null, 1);
    }

    // Constructor with description, price, and quantity
    public Servico(String descricao, double preco, int quantidade) {
        this(0, descricao, preco, null, null, quantidade);
    }

    // Existing getters and setters, plus new quantidade methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(Integer veiculoId) {
        this.veiculoId = veiculoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    // Method to calculate total price
    public double getTotal() {
        return preco * quantidade;
    }

    @Override
    public String toString() {
        return descricao + " - R$ " + preco + " x " + quantidade;
    }
}