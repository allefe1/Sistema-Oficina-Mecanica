package controller;

import dao.EmpresaDAO;
import models.Empresa;

public class EmpresaController {
    private EmpresaDAO empresaDAO;

    public EmpresaController() {
        empresaDAO = new EmpresaDAO();
    }

    public Empresa buscarEmpresa() {
        return empresaDAO.buscarEmpresa();
    }

    public void salvarEmpresa(Empresa empresa) {
        if (empresaDAO.buscarEmpresa() == null) {
            empresaDAO.salvarEmpresa(empresa);
        } else {
            empresaDAO.atualizarEmpresa(empresa);
        }
    }
}
