import entidades.Certificado;
import entidades.Coordenador;
import entidades.Curso;
import entidades.Discente;
import entidades.Docente;
import entidades.GrupoEstudantil;
import entidades.Oportunidade;
import entidades.Ppc;
import entidades.SolicitacaoAproveitamento;
import entidades.UnidadeCurricular;
import entidades.enums.CargoGrupo;
import entidades.enums.ModalidadeOportunidade;
import entidades.enums.StatusOportunidade;
import interfaceterminal.MenuTerminal;
import repositorio.RepositorioCentral;
import servicos.AproveitamentoService;
import servicos.CursoService;
import servicos.GrupoService;
import servicos.OportunidadeService;
import servicos.UsuarioService;

import java.time.LocalDate;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        RepositorioCentral repositorio = new RepositorioCentral();

        // CursoService antes do UsuarioService porque os discentes de teste
        // ja sao criados vinculados ao PPC padrao.
        CursoService cursoService = new CursoService(repositorio);
        UsuarioService usuarioService = new UsuarioService(repositorio, cursoService);
        OportunidadeService oportunidadeService = new OportunidadeService(repositorio);
        AproveitamentoService aproveitamentoService = new AproveitamentoService(repositorio);
        GrupoService grupoService = new GrupoService(repositorio);

        popularDemo(repositorio, oportunidadeService, aproveitamentoService, grupoService, cursoService);

        MenuTerminal menu = new MenuTerminal(usuarioService, oportunidadeService,
                                             aproveitamentoService, grupoService, cursoService);
        menu.iniciar();
    }

    // Popula cenarios prontos para a apresentacao. Cada bloco para em um ponto
    // diferente da maquina de estados, pra que a banca consiga continuar o fluxo
    // ao vivo sem precisar executar todos os passos anteriores.
    private static void popularDemo(RepositorioCentral repositorio,
                                    OportunidadeService oportunidadeService,
                                    AproveitamentoService aproveitamentoService,
                                    GrupoService grupoService,
                                    CursoService cursoService) {

        Discente aluno1   = (Discente)    repositorio.findUsuarioByEmail("aluno1@ufma.br");
        Discente aluno2   = (Discente)    repositorio.findUsuarioByEmail("aluno2@ufma.br");
        Docente doc       = (Docente)     repositorio.findUsuarioByEmail("doc@ufma.br");
        Coordenador coord = (Coordenador) repositorio.findUsuarioByEmail("coord1@ufma.br");

        Curso cc = repositorio.findCursoByCodigo("CC");
        Ppc ppc2020 = cc.getPpcAtual();
        UnidadeCurricular ext0001 = ppc2020.buscarUcePorCodigo("EXT0001");

        // ---- Segunda versao do PPC: mostra que o versionamento funciona.
        // aluno1/aluno2 continuam ancorados no 2020 (vira HISTORICO).
        cursoService.cadastrarNovaVersaoPpc(cc.getId(), "2025", 320, coord);
        Ppc ppc2025 = cc.buscarPpcPorAno("2025");
        cursoService.cadastrarUce(ppc2025, "EXT0003", "Atividades Extensionistas Integradas", 80);

        // ---- Grupo estudantil com lider: habilita "Propor oportunidade" no menu do discente.
        GrupoEstudantil liga = new GrupoEstudantil(
            "Liga Academica de Computacao",
            "Promove maratonas de programacao, palestras e minicursos para a comunidade do curso.",
            doc);
        liga.adicionarMembro(aluno1);
        liga.definirCargo(aluno1, CargoGrupo.PRESIDENTE);
        liga.adicionarMembro(aluno2);
        grupoService.criarGrupo(liga);

        // ---- Cenario 1: HAPPY PATH COMPLETO (aluno1 ja tem 40h computadas).
        Oportunidade op1 = new Oportunidade(
            "Curso de Java - Fundamentos",
            "Introducao a sintaxe, orientacao a objetos e bibliotecas padrao.",
            ModalidadeOportunidade.CURSO,
            "01/02/2026 - 28/02/2026",
            40, 5, doc, StatusOportunidade.ABERTA);
        op1.marcarComoUce(ext0001);
        oportunidadeService.criarOportunidade(op1);
        oportunidadeService.inscreverDiscente(op1.getId(), aluno1);
        oportunidadeService.avaliarInscricao(op1.getId(), aluno1, true);
        oportunidadeService.iniciarExecucao(op1.getId());
        oportunidadeService.encerrarOportunidade(op1.getId());
        oportunidadeService.certificarDiscentes(op1.getId(), Collections.singletonList(aluno1));
        Certificado certJava = aluno1.getCertificados().get(aluno1.getCertificados().size() - 1);
        SolicitacaoAproveitamento sJava = new SolicitacaoAproveitamento(aluno1, certJava);
        aproveitamentoService.criarSolicitacao(sJava);
        aproveitamentoService.avaliarSolicitacao(sJava, true,
            "Atividade compativel com a UCE EXT0001. Carga horaria validada.");

        // ---- Cenario 2: EM_EXECUCAO -> demo de encerrar + certificar ao vivo.
        Oportunidade op2 = new Oportunidade(
            "Minicurso de Git e Controle de Versao",
            "Fluxo de commits, branches e pull requests no dia a dia.",
            ModalidadeOportunidade.CURSO,
            "01/05/2026 - 31/05/2026",
            20, 8, doc, StatusOportunidade.ABERTA);
        oportunidadeService.criarOportunidade(op2);
        oportunidadeService.inscreverDiscente(op2.getId(), aluno1);
        oportunidadeService.avaliarInscricao(op2.getId(), aluno1, true);
        oportunidadeService.iniciarExecucao(op2.getId());

        // ---- Cenario 3: ABERTA sem inscritos -> demo de inscricao ao vivo.
        Oportunidade op3 = new Oportunidade(
            "Workshop de Algoritmos Avancados",
            "Programacao dinamica, grafos e tecnicas de otimizacao.",
            ModalidadeOportunidade.CURSO,
            "01/06/2026 - 30/06/2026",
            30, 10, doc, StatusOportunidade.ABERTA);
        oportunidadeService.criarOportunidade(op3);

        // ---- Cenario 4: ENCERRADA + aluno2 certificado sem pedir aproveitamento ->
        // demo: aluno2 loga e usa [4] Solicitar aproveitamento.
        Oportunidade op4 = new Oportunidade(
            "Palestra: Etica em Inteligencia Artificial",
            "Discussao sobre vies algoritmico, privacidade e responsabilidade.",
            ModalidadeOportunidade.EVENTO,
            "15/03/2026",
            4, 50, doc, StatusOportunidade.ABERTA);
        oportunidadeService.criarOportunidade(op4);
        oportunidadeService.inscreverDiscente(op4.getId(), aluno2);
        oportunidadeService.avaliarInscricao(op4.getId(), aluno2, true);
        oportunidadeService.iniciarExecucao(op4.getId());
        oportunidadeService.encerrarOportunidade(op4.getId());
        oportunidadeService.certificarDiscentes(op4.getId(), Collections.singletonList(aluno2));

        // ---- Cenario 5: Aproveitamento PENDENTE -> demo do coordenador avaliar.
        Oportunidade op5 = new Oportunidade(
            "Seminario de Sistemas Distribuidos",
            "Consenso, replicacao e tolerancia a falhas.",
            ModalidadeOportunidade.EVENTO,
            "10/03/2026 - 20/03/2026",
            16, 25, doc, StatusOportunidade.ABERTA);
        oportunidadeService.criarOportunidade(op5);
        oportunidadeService.inscreverDiscente(op5.getId(), aluno2);
        oportunidadeService.avaliarInscricao(op5.getId(), aluno2, true);
        oportunidadeService.iniciarExecucao(op5.getId());
        oportunidadeService.encerrarOportunidade(op5.getId());
        oportunidadeService.certificarDiscentes(op5.getId(), Collections.singletonList(aluno2));
        Certificado certSemi = aluno2.getCertificados().get(aluno2.getCertificados().size() - 1);
        SolicitacaoAproveitamento sSemi = new SolicitacaoAproveitamento(aluno2, certSemi);
        aproveitamentoService.criarSolicitacao(sSemi);

        // ---- Cenario 6: Aproveitamento DELEGADO a Comissao -> demo da comissao avaliar.
        Oportunidade op6 = new Oportunidade(
            "Curso de Python Avancado",
            "Decoradores, geradores e programacao assincrona.",
            ModalidadeOportunidade.CURSO,
            "01/04/2026 - 30/04/2026",
            24, 8, doc, StatusOportunidade.ABERTA);
        oportunidadeService.criarOportunidade(op6);
        oportunidadeService.inscreverDiscente(op6.getId(), aluno2);
        oportunidadeService.avaliarInscricao(op6.getId(), aluno2, true);
        oportunidadeService.iniciarExecucao(op6.getId());
        oportunidadeService.encerrarOportunidade(op6.getId());
        oportunidadeService.certificarDiscentes(op6.getId(), Collections.singletonList(aluno2));
        Certificado certPy = aluno2.getCertificados().get(aluno2.getCertificados().size() - 1);
        SolicitacaoAproveitamento sPy = new SolicitacaoAproveitamento(aluno2, certPy);
        aproveitamentoService.criarSolicitacao(sPy);
        aproveitamentoService.delegarParaComissao(sPy.getId());

        // ---- Cenario 7: INDEFERIDO HA 10 DIAS -> prazo de reenvio (5 dias) estourado.
        Oportunidade op7 = new Oportunidade(
            "Workshop de Banco de Dados",
            "Modelagem relacional, normalizacao e SQL pratico.",
            ModalidadeOportunidade.CURSO,
            "01/02/2026 - 15/02/2026",
            12, 15, doc, StatusOportunidade.ABERTA);
        oportunidadeService.criarOportunidade(op7);
        oportunidadeService.inscreverDiscente(op7.getId(), aluno1);
        oportunidadeService.avaliarInscricao(op7.getId(), aluno1, true);
        oportunidadeService.iniciarExecucao(op7.getId());
        oportunidadeService.encerrarOportunidade(op7.getId());
        oportunidadeService.certificarDiscentes(op7.getId(), Collections.singletonList(aluno1));
        Certificado certBd = aluno1.getCertificados().get(aluno1.getCertificados().size() - 1);
        SolicitacaoAproveitamento sBd = new SolicitacaoAproveitamento(aluno1, certBd);
        aproveitamentoService.criarSolicitacao(sBd);
        aproveitamentoService.avaliarSolicitacao(sBd, false,
            "Carga horaria insuficiente para a UCE indicada. Falta documentacao complementar.");
        // Backdata para forcar prazo de reenvio estourado.
        sBd.ajustarDatasParaDemo(LocalDate.now().minusDays(20), LocalDate.now().minusDays(10));
    }
}
