package models;

public class Empresa {
    private Integer id; // Alterado de int para Integer
    private String nome;
    private String cpfCnpj;
    private String telefone;
    private String endereco;

    // Construtores
    public Empresa() {}

    public Empresa(String nome, String cpfCnpj, String telefone, String endereco) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    // Getters e Setters
    public Integer getId() { // Retorna Integer
        return id;
    }

    public void setId(Integer id) { // Aceita Integer
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
