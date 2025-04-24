package models;

import java.util.ArrayList;
import java.util.List;

public class OrdemServico {

    private String nomeEmpresa;
    private String cnpjEmpresa;
    private String telefoneEmpresa;
    private String enderecoEmpresa;

    private String cliente;
    private String veiculo;
    private String anoVeiculo;
    private String chassiVeiculo;
    private List<Servico> servicos;
    private double total;
    private String endereco;
    private String telefone;
    private double desconto;
    private int numeroOrdem;

    private String assinaturaMecanico;
    private String assinaturaCliente;

    public OrdemServico() {
        this.servicos = new ArrayList<>();
        this.total = 0.0;
    }

    // Getters e Setters
    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getCnpjEmpresa() {
        return cnpjEmpresa;
    }

    public void setCnpjEmpresa(String cnpjEmpresa) {
        this.cnpjEmpresa = cnpjEmpresa;
    }

    public String getTelefoneEmpresa() {
        return telefoneEmpresa;
    }

    public void setTelefoneEmpresa(String telefoneEmpresa) {
        this.telefoneEmpresa = telefoneEmpresa;
    }

    public String getEnderecoEmpresa() {
        return enderecoEmpresa;
    }

    public void setEnderecoEmpresa(String enderecoEmpresa) {
        this.enderecoEmpresa = enderecoEmpresa;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }
    
 // Getters e Setters
    public String getAnoVeiculo() {
        return anoVeiculo;
    }

    public void setAnoVeiculo(String anoVeiculo) {
        this.anoVeiculo = anoVeiculo;
    }

    public String getChassiVeiculo() {
        return chassiVeiculo;
    }

    public void setChassiVeiculo(String chassiVeiculo) {
        this.chassiVeiculo = chassiVeiculo;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }
    
    public int getNumeroOrdem() {
        return numeroOrdem;
    }

    public void setNumeroOrdem(int numeroOrdem) {
        this.numeroOrdem = numeroOrdem;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
        atualizarTotal();
    }

    public void adicionarServico(Servico servico) {
        this.servicos.add(servico);
        atualizarTotal();
    }

    public double getTotal() {
        return total;
    }

    private void atualizarTotal() {
        this.total = servicos.stream()
                .mapToDouble(Servico::getPreco) // Usa o método correto para obter o preço
                .sum();
    }
    
    public double getTotalComDesconto() {
        double total = 0;
        for (Servico servico : getServicos()) {
            total += servico.getTotal();
        }
        return total - desconto;
    }

    // Método para calcular o subtotal (sem desconto)
    public double getSubtotal() {
        double total = 0;
        for (Servico servico : getServicos()) {
            total += servico.getTotal();
        }
        return total;
    }

    public String getAssinaturaMecanico() {
        return assinaturaMecanico;
    }

    public void setAssinaturaMecanico(String assinaturaMecanico) {
        this.assinaturaMecanico = assinaturaMecanico;
    }

    public String getAssinaturaCliente() {
        return assinaturaCliente;
    }

    public void setAssinaturaCliente(String assinaturaCliente) {
        this.assinaturaCliente = assinaturaCliente;
    }
}
