package br.ufma.extensao.config;

import br.ufma.extensao.model.Certificado;
import br.ufma.extensao.model.Curso;
import br.ufma.extensao.model.Discente;
import br.ufma.extensao.model.GrupoEstudantil;
import br.ufma.extensao.model.Oportunidade;
import br.ufma.extensao.model.Papel;
import br.ufma.extensao.model.SolicitacaoAproveitamento;
import br.ufma.extensao.model.Usuario;
import br.ufma.extensao.model.enums.CargoGrupo;
import br.ufma.extensao.model.enums.ModalidadeOportunidade;
import br.ufma.extensao.model.enums.StatusOportunidade;
import br.ufma.extensao.repo.DiscenteRepository;
import br.ufma.extensao.repo.SolicitacaoAproveitamentoRepository;
import br.ufma.extensao.repo.UsuarioRepository;
import br.ufma.extensao.service.CursoService;
import br.ufma.extensao.service.DiscenteService;
import br.ufma.extensao.service.GrupoEstudantilService;
import br.ufma.extensao.service.OportunidadeService;
import br.ufma.extensao.service.PapelService;
import br.ufma.extensao.service.SolicitacaoAproveitamentoService;
import br.ufma.extensao.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

// Popula os 7 cenarios de demonstracao da P2. So roda com o banco vazio. Senha de todos: "123".
@Component
@Order(1)
public class DemoDataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private DiscenteService discenteService;
    @Autowired
    private CursoService cursoService;
    @Autowired
    private PapelService papelService;
    @Autowired
    private OportunidadeService oportunidadeService;
    @Autowired
    private SolicitacaoAproveitamentoService aproveitamentoService;
    @Autowired
    private GrupoEstudantilService grupoService;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private DiscenteRepository discenteRepository;
    @Autowired
    private SolicitacaoAproveitamentoRepository aproveitamentoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) return; // ja semeado

        // ===== Papeis =====
        Papel pDocente = papelService.salvar(Papel.builder().nome("DOCENTE").build());
        Papel pCoord = papelService.salvar(Papel.builder().nome("COORDENADOR").build());
        Papel pComissao = papelService.salvar(Papel.builder().nome("COMISSAO").build());
        Papel pAdmin = papelService.salvar(Papel.builder().nome("ADMINISTRADOR").build());

        // ===== Curso (PPC/UCE colapsados em Curso) =====
        Curso cc = cursoService.salvar(Curso.builder()
                .nome("Ciencia da Computacao").curriculo("2020").cargaHoraria(320).build());

        // ===== Usuarios por papel =====
        Usuario doc = novoUsuario("Prof. Joao Docente", "doc@ufma.br", "DOC001", List.of(pDocente));
        novoUsuario("Maria Coordenadora", "coord1@ufma.br", "COORD001", List.of(pCoord));
        novoUsuario("Carlos Comissao", "comissao@ufma.br", "COM001", List.of(pComissao));
        novoUsuario("Admin do Sistema", "admin@ufma.br", "ADM001", List.of(pAdmin));

        // ===== Discentes =====
        Discente aluno1 = novoDiscente("Ana Aluno", "aluno1@ufma.br", "2020001", cc);
        Discente aluno2 = novoDiscente("Bruno Aluno", "aluno2@ufma.br", "2020002", cc);

        // ===== Grupo estudantil (liga com lider) =====
        GrupoEstudantil liga = grupoService.salvar(GrupoEstudantil.builder()
                .nome("Liga Academica de Computacao")
                .descricao("Promove maratonas de programacao, palestras e minicursos para a comunidade do curso.")
                .responsavel(doc).build());
        grupoService.adicionarMembro(liga.getId(), aluno1.getId());
        grupoService.definirCargo(liga.getId(), aluno1.getId(), CargoGrupo.PRESIDENTE);
        grupoService.adicionarMembro(liga.getId(), aluno2.getId());

        // ===== Cenario 1: fluxo completo - aluno1 com 40h DEFERIDAS =====
        String t1 = "Curso de Java - Fundamentos";
        Integer op1 = abrir(t1, "Introducao a sintaxe, orientacao a objetos e bibliotecas padrao.",
                ModalidadeOportunidade.CURSO, "01/02/2026 - 28/02/2026", 40, 5, doc);
        oportunidadeService.inscrever(op1, aluno1.getId());
        oportunidadeService.avaliarInscricao(op1, aluno1.getId(), true);
        oportunidadeService.iniciar(op1);
        oportunidadeService.encerrar(op1);
        oportunidadeService.certificar(op1, List.of(aluno1.getId()));
        SolicitacaoAproveitamento sJava = abrirSolicitacao(aluno1.getId(), t1);
        aproveitamentoService.avaliar(sJava.getId(), true,
                "Atividade compativel com a UCE. Carga horaria validada.");

        // ===== Cenario 2: EM_EXECUCAO - demo de encerrar/certificar ao vivo =====
        Integer op2 = abrir("Minicurso de Git e Controle de Versao",
                "Fluxo de commits, branches e pull requests no dia a dia.",
                ModalidadeOportunidade.CURSO, "01/05/2026 - 31/05/2026", 20, 8, doc);
        oportunidadeService.inscrever(op2, aluno1.getId());
        oportunidadeService.avaliarInscricao(op2, aluno1.getId(), true);
        oportunidadeService.iniciar(op2);

        // ===== Cenario 3: ABERTA sem inscrito - demo de inscricao ao vivo =====
        abrir("Workshop de Algoritmos Avancados",
                "Programacao dinamica, grafos e tecnicas de otimizacao.",
                ModalidadeOportunidade.CURSO, "01/06/2026 - 30/06/2026", 30, 10, doc);

        // ===== Cenario 4: aluno2 CERTIFICADO mas ainda nao pediu aproveitamento =====
        Integer op4 = abrir("Palestra: Etica em Inteligencia Artificial",
                "Discussao sobre vies algoritmico, privacidade e responsabilidade.",
                ModalidadeOportunidade.EVENTO, "15/03/2026", 4, 50, doc);
        oportunidadeService.inscrever(op4, aluno2.getId());
        oportunidadeService.avaliarInscricao(op4, aluno2.getId(), true);
        oportunidadeService.iniciar(op4);
        oportunidadeService.encerrar(op4);
        oportunidadeService.certificar(op4, List.of(aluno2.getId()));

        // ===== Cenario 5: aproveitamento PENDENTE para o coordenador avaliar =====
        String t5 = "Seminario de Sistemas Distribuidos";
        Integer op5 = abrir(t5, "Consenso, replicacao e tolerancia a falhas.",
                ModalidadeOportunidade.EVENTO, "10/03/2026 - 20/03/2026", 16, 25, doc);
        oportunidadeService.inscrever(op5, aluno2.getId());
        oportunidadeService.avaliarInscricao(op5, aluno2.getId(), true);
        oportunidadeService.iniciar(op5);
        oportunidadeService.encerrar(op5);
        oportunidadeService.certificar(op5, List.of(aluno2.getId()));
        abrirSolicitacao(aluno2.getId(), t5); // fica PENDENTE

        // ===== Cenario 6: aproveitamento DELEGADO para a comissao =====
        String t6 = "Curso de Python Avancado";
        Integer op6 = abrir(t6, "Decoradores, geradores e programacao assincrona.",
                ModalidadeOportunidade.CURSO, "01/04/2026 - 30/04/2026", 24, 8, doc);
        oportunidadeService.inscrever(op6, aluno2.getId());
        oportunidadeService.avaliarInscricao(op6, aluno2.getId(), true);
        oportunidadeService.iniciar(op6);
        oportunidadeService.encerrar(op6);
        oportunidadeService.certificar(op6, List.of(aluno2.getId()));
        SolicitacaoAproveitamento sPy = abrirSolicitacao(aluno2.getId(), t6);
        aproveitamentoService.delegar(sPy.getId());

        // ===== Cenario 7: INDEFERIDA com prazo de reenvio ja estourado =====
        String t7 = "Workshop de Banco de Dados";
        Integer op7 = abrir(t7, "Modelagem relacional, normalizacao e SQL pratico.",
                ModalidadeOportunidade.CURSO, "01/02/2026 - 15/02/2026", 12, 15, doc);
        oportunidadeService.inscrever(op7, aluno1.getId());
        oportunidadeService.avaliarInscricao(op7, aluno1.getId(), true);
        oportunidadeService.iniciar(op7);
        oportunidadeService.encerrar(op7);
        oportunidadeService.certificar(op7, List.of(aluno1.getId()));
        SolicitacaoAproveitamento sBd = abrirSolicitacao(aluno1.getId(), t7);
        aproveitamentoService.avaliar(sBd.getId(), false,
                "Carga horaria insuficiente. Falta documentacao complementar.");
        // forca datas antigas para simular o prazo de reenvio (5 dias) estourado
        sBd.setDataCriacao(LocalDate.now().minusDays(20));
        sBd.setDataAvaliacao(LocalDate.now().minusDays(10));
        aproveitamentoRepository.save(sBd);
    }

    // ===== Helpers =====

    private Usuario novoUsuario(String nome, String email, String matricula, List<Papel> papeis) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setMatricula(matricula);
        u.setSenha("123");
        u.setAtivo(true);
        u.setPapeis(papeis);
        return usuarioService.salvar(u);
    }

    private Discente novoDiscente(String nome, String email, String matricula, Curso curso) {
        Discente d = new Discente();
        d.setNome(nome);
        d.setEmail(email);
        d.setMatricula(matricula);
        d.setSenha("123");
        d.setAtivo(true);
        d.setCurso(curso);
        d.setHorasCumpridas(0);
        return discenteService.salvar(d);
    }

    private Integer abrir(String titulo, String descricao, ModalidadeOportunidade modalidade,
                          String periodo, int cargaHoraria, int vagas, Usuario responsavel) {
        Oportunidade o = new Oportunidade();
        o.setTitulo(titulo);
        o.setDescricao(descricao);
        o.setModalidade(modalidade);
        o.setPeriodoRealizacao(periodo);
        o.setCargaHoraria(cargaHoraria);
        o.setVagas(vagas);
        o.setResponsavel(responsavel);
        o.setStatus(StatusOportunidade.ABERTA);
        return oportunidadeService.salvar(o).getId();
    }

    // cria a solicitacao de aproveitamento sobre o certificado daquela atividade
    private SolicitacaoAproveitamento abrirSolicitacao(Integer discenteId, String tituloAtividade) {
        Discente d = discenteRepository.findById(discenteId)
                .orElseThrow(() -> new IllegalStateException("Discente do seed nao encontrado."));
        Certificado cert = d.getCertificados().stream()
                .filter(c -> tituloAtividade.equals(c.getTituloAtividade()))
                .reduce((primeiro, ultimo) -> ultimo)
                .orElseThrow(() -> new IllegalStateException("Certificado do seed nao encontrado: " + tituloAtividade));
        SolicitacaoAproveitamento s = new SolicitacaoAproveitamento();
        s.setSolicitante(d);
        s.setCertificado(cert);
        return aproveitamentoService.salvar(s);
    }
}
