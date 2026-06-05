package br.ufma.servico;

import br.ufma.model.entidades.Discente;
import br.ufma.model.entidades.GrupoEstudantil;
import br.ufma.model.entidades.SolicitacaoGrupoEstudantil;
import br.ufma.model.entidades.Usuario;
import br.ufma.model.entidades.enums.CargoGrupo;
import br.ufma.model.entidades.enums.StatusSolicitacao;
import br.ufma.model.repositorio.RepositorioCentral;

import java.util.ArrayList;
import java.util.List;

public class GrupoService {

    private final RepositorioCentral repositorio;

    public GrupoService(RepositorioCentral repositorio) {
        this.repositorio = repositorio;
    }

    public void solicitarCriacaoGrupo(SolicitacaoGrupoEstudantil solicitacao){
        repositorio.salvarSolicitacaoGrupo(solicitacao);
    }

    public List<SolicitacaoGrupoEstudantil> listarSolicitacoesPendentes(){
        List<SolicitacaoGrupoEstudantil> pendentes = new ArrayList<>();
        for (SolicitacaoGrupoEstudantil solicitacao : repositorio.findAllSolicitacoesGrupo()){
            if (solicitacao.getStatus() == StatusSolicitacao.PENDENTE){
                pendentes.add(solicitacao);
            }
        }
        return pendentes;
    }

    public void avaliarSolicitacaoGrupo(int id, boolean aprovado){
        SolicitacaoGrupoEstudantil solicitacao = repositorio.findSolicitacaoGrupoById(id);
        if ( solicitacao != null && solicitacao.getStatus() == StatusSolicitacao.PENDENTE){
            if(aprovado){
                solicitacao.aprovar();
                // usa os dados da solicitacao pra criar o grupo
                GrupoEstudantil novoGrupo = new GrupoEstudantil(
                    solicitacao.getNomeGrupo(),
                    solicitacao.getDescricao(),
                    solicitacao.getDocenteResponsavel()
                );
                novoGrupo.adicionarMembro(solicitacao.getSolicitante());
                novoGrupo.definirCargo(solicitacao.getSolicitante(), CargoGrupo.PRESIDENTE);
                criarGrupo(novoGrupo);
            }else{
                solicitacao.reprovar();
            }
        }
    }

    public void criarGrupo(GrupoEstudantil g) {
        repositorio.salvarGrupo(g);
    }

    public List<GrupoEstudantil> listarTodos() {
        return repositorio.findAllGrupos();
    }

    public List<GrupoEstudantil> listarPorUsuario(Usuario u) {
        return repositorio.findGrupoByUsuario(u);
    }

    public GrupoEstudantil buscarPorId(int id) {
        return repositorio.findGrupoById(id);
    }

    public boolean isLider(Discente d) {
        for (GrupoEstudantil g : repositorio.findAllGrupos()) {
            CargoGrupo cargo = g.getCargo(d);
            if (cargo != null && cargo != CargoGrupo.MEMBRO) return true;
        }
        return false;
    }
}