package interfaceterminal;

import entidades.*;
import entidades.enums.CargoGrupo;
import entidades.enums.ModalidadeOportunidade;
import entidades.enums.StatusOportunidade;
import entidades.enums.StatusSolicitacao;
import servicos.AproveitamentoService;
import servicos.CursoService;
import servicos.GrupoService;
import servicos.OportunidadeService;
import servicos.UsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuTerminal {

    private final UsuarioService usuarioService;
    private final OportunidadeService oportunidadeService;
    private final AproveitamentoService aproveitamentoService;
    private final GrupoService grupoService;
    private final CursoService cursoService;
    private final Scanner sc;

    // Usuario atualmente logado - usado para registrar autor de alteracoes (RF008)
    private Usuario usuarioLogadoAtual = null;

    public MenuTerminal(UsuarioService usuarioService,
                        OportunidadeService oportunidadeService,
                        AproveitamentoService aproveitamentoService,
                        GrupoService grupoService,
                        CursoService cursoService) {
        this.usuarioService = usuarioService;
        this.oportunidadeService = oportunidadeService;
        this.aproveitamentoService = aproveitamentoService;
        this.grupoService = grupoService;
        this.cursoService = cursoService;
        this.sc = new Scanner(System.in);
    }

    // Tela Inicial

    public void iniciar() {
        while (true) {
            System.out.println("\n=== SISTEMA DE EXTENSAO UFMA ===");
            System.out.println("[1] Login");
            System.out.println("[2] AutoCadastrar(Discente)");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = lerOpcao();

            switch (op) {
                case "1":
                    fazerLogin();
                    break;
                case "2":
                    autoCadastrar();
                    break;
                case "0":
                    System.out.println("Encerrando...");
                    return;
                default:
                    System.out.println("Opcao invalida. Tente novamente.");
                    break;
            }
        }
    }

    // Métodos de Login/Cadastro

    private void fazerLogin() {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Senha: ");
        String senha = sc.nextLine().trim();

        Usuario u = usuarioService.autenticar(email, senha);

        if (u == null) {
            System.out.println("Email ou senha invalidos.");
            return;
        }

        usuarioLogadoAtual = u;
        System.out.println("Bem-vindo, " + u.getNome() + "!");

        switch (u.getTipo()) {
            case "DISCENTE":
                menuDiscente((Discente) u);
                break;
            case "DOCENTE":
                menuDocente((Docente) u);
                break;
            case "COORDENADOR":
                menuCoordenador((Coordenador) u);
                break;
            case "COMISSAO":
                menuComissao((Comissao) u);
                break;
            case "ADMINISTRADOR":
                menuAdministrador((Administrador) u);
                break;
            default:
                System.out.println("Tipo de usuario desconhecido.");
                break;
        }
        usuarioLogadoAtual = null;
    }

    private void autoCadastrar(){
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();
        System.out.print("Matricula: ");
        String matricula = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Senha: ");
        String senha = sc.nextLine().trim();

        Ppc ppc = selecionarPpc();
        // Permite cadastro sem PPC vinculado; o painel cai no fallback padrao

        Discente novo = new Discente(nome, matricula, email, senha, ppc);

        boolean ok = usuarioService.cadastrarUsuario(novo);
        if (ok)
            System.out.println("Cadastro realizado com sucesso!");
        else
            System.out.println("Email já cadastrado.");
    }

    /**
     * RF0009 - seleciona um PPC especifico para o discente, suportando turmas
     * de PPCs diferentes (ex: 2020 e 2025) coexistindo no sistema.
     */
    private Ppc selecionarPpc() {
        List<Curso> cursos = cursoService.listarTodos();
        if (cursos.isEmpty()) {
            System.out.println("(Nenhum curso cadastrado - cadastro seguira sem PPC vinculado)");
            return null;
        }
        System.out.println("Selecione o curso:");
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println("[" + i + "] " + cursos.get(i));
        }
        System.out.println("[-1] Nao selecionar agora");
        System.out.print("Opcao: ");
        String entrada = sc.nextLine().trim();
        Curso curso;
        try {
            int idx = Integer.parseInt(entrada);
            if (idx == -1) return null;
            curso = cursoService.buscarPorIndice(idx);
        } catch (NumberFormatException e) {
            return null;
        }
        if (curso == null) return null;

        List<Ppc> versoes = curso.getVersoesPpc();
        if (versoes.isEmpty()) {
            System.out.println("(Curso sem PPC cadastrado)");
            return null;
        }
        if (versoes.size() == 1) {
            return versoes.get(0); // unica opcao - vincula direto
        }
        System.out.println("Selecione a versao do PPC:");
        for (int i = 0; i < versoes.size(); i++) {
            Ppc p = versoes.get(i);
            String tag = p.isAtiva() ? " (VIGENTE)" : " (HISTORICA)";
            System.out.println("[" + i + "] " + p.getAnoVigencia() + " - " +
                               p.getHorasExtensaoNecessarias() + "h" + tag);
        }
        System.out.print("Opcao: ");
        try {
            int idxPpc = Integer.parseInt(sc.nextLine().trim());
            if (idxPpc >= 0 && idxPpc < versoes.size()) return versoes.get(idxPpc);
        } catch (NumberFormatException e) {
            // ignora
        }
        return null;
    }

    private void cadastrarUsuario() {
        System.out.print("Nome: ");
        String nome = sc.nextLine().trim();
        System.out.print("Matricula: ");
        String matricula = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Senha: ");
        String senha = sc.nextLine().trim();
        System.out.println("Tipo: [1] Discente  [2] Docente  [3] Coordenador  [4] Comissao");
        System.out.print("Opcao: ");

        String tipo = sc.nextLine().trim();
        Usuario novo;

        switch (tipo) {
            case "1":
                Ppc ppc = selecionarPpc();
                novo = new Discente(nome, matricula, email, senha, ppc);
                break;
            case "2":
                novo = new Docente(nome, matricula, email, senha);
                break;
            case "3":
                novo = new Coordenador(nome, matricula, email, senha);
                break;
            case "4":
                novo = new Comissao(nome, matricula, email, senha);
                break;
            default:
                System.out.println("Tipo invalido.");
                return;
        }

        boolean ok = usuarioService.cadastrarUsuario(novo);
        if (ok)
            System.out.println("Cadastro realizado com sucesso!");
        else
            System.out.println("Email já cadastrado.");
    }

    // Menus

    private void menuDiscente(Discente d) {
        while (true) {
            System.out.println("\n--- MENU DISCENTE: " + d.getNome() + " ---");
            System.out.println("[01] Ver oportunidades abertas");
            System.out.println("[02] Inscrever-se em oportunidade");
            System.out.println("[03] Cancelar inscricao");
            System.out.println("[04] Solicitar aproveitamento de certificado");
            System.out.println("[05] Ver minhas solicitacoes");
            System.out.println("[06] Reenviar solicitacao indeferida");
            System.out.println("[07] Meus certificados");
            System.out.println("[08] Minhas horas cumpridas");
            System.out.println("[09] Propor nova oportunidade de extensao");
            System.out.println("[10] Solicitar criacao de grupo estudantil");
            System.out.println("[11] Gerenciar inscritos em minha oportunidade");
            System.out.println("[12] Cancelar solicitacao de aproveitamento");
            System.out.println("[13] Painel de progresso de horas");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = lerOpcao();

            switch (op) {
                case "1":
                    listarOportunidades();
                    break;
                case "2":
                    inscreverEmOportunidade(d);
                    break;
                case "3":
                    cancelarInscricao(d);
                    break;
                case "4":
                    solicitarAproveitamento(d);
                    break;
                case "5":
                    verSolicitacoesDiscente(d);
                    break;
                case "6":
                    reenviarSolicitacao(d);
                    break;
                case "7":
                    verCertificados(d);
                    break;
                case "8":
                    System.out.println("Horas cumpridas: " + d.getHorasCumpridas() + "h");
                    break;
                case "9":
                    proporOportunidade(d);
                    break;
                case "10":
                    solicitarCriacaoGrupo(d);
                    break;
                case "11":
                    avaliarInscricoesOportunidade(d);
                    break;
                case "12":
                    cancelarSolicitacaoAproveitamento(d);
                    break;
                case "13":
                    exibirPainelProgresso(d);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void menuDocente(Docente doc) {
        while (true) {
            System.out.println("\n--- MENU DOCENTE: " + doc.getNome() + " ---");
            System.out.println("[1] Criar oportunidade");
            System.out.println("[2] Ver oportunidades");
            System.out.println("[3] Aprovar propostas de oportunidades de discentes");
            System.out.println("[4] Iniciar execucao de oportunidade");
            System.out.println("[5] Encerrar oportunidade");
            System.out.println("[6] Cancelar oportunidade");
            System.out.println("[7] Ver grupos");
            System.out.println("[8] Ver membros de grupo");
            System.out.println("[9] Gerenciar membros de grupo");
            System.out.println("[10] Ver historico de cargos de um grupo");
            System.out.println("[11] Avaliar inscricoes pendentes em minhas oportunidades");
            System.out.println("[12] Substituir participante");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = lerOpcao();

            switch (op) {
                case "1":
                    criarOportunidade(doc, StatusOportunidade.ABERTA);
                    break;
                case "2":
                    listarOportunidades();
                    break;
                case "3":
                    aprovarOportunidadesPendentes();
                    break;
                case "4":
                    iniciarExecucaoOportunidade();
                    break;
                case "5":
                    encerrarOportunidade();
                    break;
                case "6":
                    cancelarOportunidade(doc);
                    break;
                case "7":
                    listarGrupos();
                    break;
                case "8":
                    listarGrupo();
                    break;
                case "9":
                    gerenciarGrupo(doc);
                    break;
                case "10":
                    verHistoricoCargos();
                    break;
                case "11":
                    avaliarInscricoesOportunidade(doc);
                    break;
                case "12":
                    substituirParticipante(doc);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void menuCoordenador(Coordenador c) {
        while (true) {
            System.out.println("\n--- MENU COORDENADOR: " + c.getNome() + " ---");
            System.out.println("[1] Ver solicitacoes pendentes (nao delegadas)");
            System.out.println("[2] Avaliar solicitacao de aproveitamento");
            System.out.println("[3] Delegar solicitacao para a Comissao");
            System.out.println("[4] Avaliar solicitacoes de criacao de grupos");
            System.out.println("[5] Criar Grupo Estudantil");
            System.out.println("[6] Ver grupos");
            System.out.println("[7] Gerenciar Cursos (PPC)");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = lerOpcao();

            switch (op) {
                case "1":
                    listarSolicitacoesPendentesSemDelegacao();
                    break;
                case "2":
                    avaliarSolicitacao();
                    break;
                case "3":
                    delegarSolicitacaoParaComissao();
                    break;
                case "4":
                    avaliarSolicitacaoGrupos();
                    break;
                case "5":
                    criarGrupo();
                    break;
                case "6":
                    listarGrupos();
                    break;
                case "7":
                    menuGerenciarCursos();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void menuComissao(Comissao c) {
        while (true) {
            System.out.println("\n--- MENU COMISSAO: " + c.getNome() + " ---");
            System.out.println("[1] Ver solicitacoes delegadas pelo Coordenador");
            System.out.println("[2] Avaliar solicitacao delegada");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = lerOpcao();

            switch (op) {
                case "1":
                    listarSolicitacoesDelegadas();
                    break;
                case "2":
                    avaliarSolicitacaoDelegada();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void menuAdministrador(Administrador a) {
        while (true) {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("[01] Cadastrar Usuário");
            System.out.println("[02] Listar todos os usuarios");
            System.out.println("[03] Desativar Usuario");
            System.out.println("[04] Avaliar solicitacao de criação de grupos");
            System.out.println("[05] Criar oportunidade");
            System.out.println("[06] Ver oportunidades");
            System.out.println("[07] Aprovar propostas de oportunidades de discentes");
            System.out.println("[08] Iniciar execucao de oportunidade");
            System.out.println("[09] Encerrar oportunidade");
            System.out.println("[10] Cancelar oportunidade");
            System.out.println("[11] Ver solicitacoes pendentes de aproveitamento");
            System.out.println("[12] Avaliar solicitacao de aproveitamento");
            System.out.println("[13] Criar grupo estudantil");
            System.out.println("[14] Ver grupos");
            System.out.println("[15] Ver grupo");
            System.out.println("[16] Gerenciar membros de grupo");
            System.out.println("[17] Ver historico de cargos de um grupo");
            System.out.println("[18] Substituir participante em oportunidade");
            System.out.println("[19] Gerenciar Cursos (PPC)");
            System.out.println("[0]  Sair");
            System.out.print("Opcao: ");

            String op = lerOpcao();

            switch (op) {
                case "1":
                    cadastrarUsuario();
                    break;
                case "2":
                    listarTodosUsuarios();
                    break;
                case "3":
                    desativarUsuario();
                    break;
                case "4":
                    avaliarSolicitacaoGrupos();
                    break;
                case "5":
                    criarOportunidadeAdmin();
                    break;
                case "6":
                    listarOportunidades();
                    break;
                case "7":
                    aprovarOportunidadesPendentes();
                    break;
                case "8":
                    iniciarExecucaoOportunidade();
                    break;
                case "9":
                    encerrarOportunidade();
                    break;
                case "10":
                    cancelarOportunidade(a);
                    break;
                case "11":
                    listarSolicitacoesPendentes();
                    break;
                case "12":
                    avaliarSolicitacao();
                    break;
                case "13":
                    criarGrupo();
                    break;
                case "14":
                    listarGrupos();
                    break;
                case "15":
                    listarGrupo();
                    break;
                case "16":
                    gerenciarGrupo(a);
                    break;
                case "17":
                    verHistoricoCargos();
                    break;
                case "18":
                    substituirParticipante(a);
                    break;
                case "19":
                    menuGerenciarCursos();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    // Métodos do Negócio

    //Usuário

    private void desativarUsuario() {
        System.out.println("Insira o email do usuário a desativar: ");
        String email = sc.nextLine().trim();
        usuarioService.desativarUsuario(email);
        System.out.println("Desativado com sucesso!");
    }

    // Oportunidade

    private void proporOportunidade(Discente d){
        if (!grupoService.isLider(d)) {
            System.out.println("Apenas lideres de grupos estudantis podem propor oportunidades.");
            return;
        }
        System.out.println("--- Propor Oportunidade ---");
        criarOportunidade(d, StatusOportunidade.AGUARDANDO_APROVACAO);
        System.out.println("A sua proposta foi enviada para aprovacao docente.");
    }

    private void aprovarOportunidadesPendentes(){
        List<Oportunidade> pendentes = oportunidadeService.listarAguardandoAprovacao();
        if (pendentes.isEmpty()){
            System.out.println("Não há propostas pendentes");
            return;
        }
        for(Oportunidade p : pendentes){
            System.out.println(p);
        }

        System.out.println("ID da oportunidade a aprovar (ou 0 para cancelar): ");
        int id = lerInt();
        if(id>0){
            oportunidadeService.aprovarOportunidade(id);
            System.out.println("Oportunidade publicada com sucesso!");
        }
    }

    private void listarOportunidades() {
        List<Oportunidade> lista = oportunidadeService.listarTodas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma oportunidade cadastrada.");
            return;
        }
        System.out.println("-- Oportunidades --");
        for (Oportunidade o : lista) {
            System.out.println(o);
        }
    }

    private void criarOportunidade(Usuario responsavel, StatusOportunidade status) {
        System.out.print("Titulo: ");
        String titulo = sc.nextLine().trim();

        System.out.print("Descricao: ");
        String descricao = sc.nextLine().trim();

        System.out.println("Modalidade:");
        ModalidadeOportunidade[] modalidades = ModalidadeOportunidade.values();
        for (int i = 0; i < modalidades.length; i++) {
            System.out.println("[" + i + "] " + modalidades[i]);
        }
        System.out.print("Opcao: ");
        int idxMod = lerInt();
        if (idxMod < 0 || idxMod >= modalidades.length) {
            System.out.println("Modalidade invalida.");
            return;
        }
        ModalidadeOportunidade modalidade = modalidades[idxMod];

        System.out.print("Periodo de realizacao (ex: 01/06/2026 - 30/06/2026): ");
        String periodo = sc.nextLine().trim();

        System.out.print("Carga horaria: ");
        int ch = lerIntMaiorQueZero();

        System.out.print("Vagas: ");
        int vagas = lerInt();

        Oportunidade nova = new Oportunidade(titulo, descricao, modalidade, periodo, ch, vagas, responsavel, status);

        // RF009/RF0009 - opcionalmente vincular como UCE (somente quando criada por Docente/Coordenador/Admin)
        if (!(responsavel instanceof Discente)) {
            System.out.print("Esta oportunidade e uma UCE (Unidade Curricular de Extensao)? (s/n): ");
            String resp = sc.nextLine().trim().toLowerCase();
            if (resp.equals("s")) {
                System.out.println("[1] Vincular a uma UCE concreta de um PPC (recomendado)");
                System.out.println("[2] Apenas indicar componente curricular como texto livre");
                System.out.print("Opcao: ");
                String tipoUce = sc.nextLine().trim();
                if (tipoUce.equals("1")) {
                    UnidadeCurricular u = selecionarUceDePpc();
                    if (u != null) nova.marcarComoUce(u);
                } else if (tipoUce.equals("2")) {
                    System.out.print("Codigo/nome do componente curricular: ");
                    String cc = sc.nextLine().trim();
                    if (!cc.isEmpty()) nova.marcarComoUce(cc);
                }
            }
        }

        oportunidadeService.criarOportunidade(nova);
        if (status == StatusOportunidade.ABERTA) {
            System.out.println("Oportunidade aberta com sucesso!");
        }
    }

    // RF0009 - escolhe uma UCE concreta de algum PPC do sistema
    private UnidadeCurricular selecionarUceDePpc() {
        List<Curso> cursos = cursoService.listarTodos();
        if (cursos.isEmpty()) { System.out.println("Nenhum curso cadastrado."); return null; }
        System.out.println("Selecione o curso:");
        for (int i = 0; i < cursos.size(); i++) {
            System.out.println("[" + i + "] " + cursos.get(i));
        }
        System.out.print("Opcao: ");
        int idxCurso = lerInt();
        Curso c = cursoService.buscarPorIndice(idxCurso);
        if (c == null) return null;

        List<Ppc> versoes = c.getVersoesPpc();
        if (versoes.isEmpty()) { System.out.println("Curso sem PPC."); return null; }
        System.out.println("Selecione o PPC:");
        for (int i = 0; i < versoes.size(); i++) {
            System.out.println("[" + i + "] " + versoes.get(i).getAnoVigencia()
                              + (versoes.get(i).isAtiva() ? " (vigente)" : " (historica)"));
        }
        System.out.print("Opcao: ");
        int idxPpc = lerInt();
        if (idxPpc < 0 || idxPpc >= versoes.size()) return null;
        Ppc ppc = versoes.get(idxPpc);

        List<UnidadeCurricular> uces = ppc.getUces();
        if (uces.isEmpty()) { System.out.println("PPC sem UCEs."); return null; }
        System.out.println("Selecione a UCE:");
        for (int i = 0; i < uces.size(); i++) {
            System.out.println("[" + i + "] " + uces.get(i));
        }
        System.out.print("Opcao: ");
        int idxUce = lerInt();
        if (idxUce >= 0 && idxUce < uces.size()) return uces.get(idxUce);
        return null;
    }

    private void criarOportunidadeAdmin() {
        List<Docente> docentes = usuarioService.listarDocentes();
        if (docentes.isEmpty()) {
            System.out.println("Nenhum docente cadastrado.");
            return;
        }

        System.out.println("Selecione o docente responsavel:");
        for (int i = 0; i < docentes.size(); i++) {
            System.out.println("[" + i + "] " + docentes.get(i).getNome());
        }
        System.out.print("Opcao: ");
        int idx = lerInt();
        Docente doc = usuarioService.buscarDocentePorIndice(idx);

        if (doc == null) {
            System.out.println("Docente invalido.");
            return;
        }

        criarOportunidade(doc, StatusOportunidade.ABERTA);
    }

    private void avaliarInscricoesOportunidade(Usuario responsavel){
        listarOportunidades();
        System.out.println("Insira o ID da sua oportunidade: ");
        int id = lerInt();
        Oportunidade op = oportunidadeService.buscarPorId(id);

        if(op == null || !op.getResponsavel().equals(responsavel)){
            System.out.println("Oportunidade invalida ou voce nao é o responsavel.");
            return;
        }

        List<Discente> espera = op.getFilaEspera();
        if (espera.isEmpty()){
            System.out.println("Não há discentes na fila de espera");
            return;
        }

        System.out.println("Fila de Espera: ");
        for (int i = 0; i < espera.size(); i++) {
            System.out.println("[" + i + "] " + espera.get(i).getNome());
        }

        System.out.println("Selecione o indice do discente: ");
        int idx = lerInt();
        if (idx>=0 && idx<espera.size()){
            Discente disc = espera.get(idx);
            System.out.println("Aprovar inscrição?(1-Sim, 0-Não): ");
            int resposta = lerInt();
            if (resposta==1){
                oportunidadeService.avaliarInscricao(id,disc,true);
            }else if(resposta==0){
                oportunidadeService.avaliarInscricao(id,disc,false);
            }else{
                System.out.println("Opcao invalida.");
            }
        }else{
            System.out.println("Indice invalido");
        }

    }

    private void iniciarExecucaoOportunidade() {
        listarOportunidades();
        System.out.print("ID da oportunidade a iniciar execucao: ");
        int id = lerInt();
        boolean ok = oportunidadeService.iniciarExecucao(id);
        if (ok)
            System.out.println("Oportunidade marcada como EM_EXECUCAO.");
        else
            System.out.println("Nao foi possivel iniciar execucao (oportunidade inexistente ou nao esta ABERTA).");
    }

    /**
     * RF012 - cancela uma oportunidade. So permite enquanto nao foi encerrada/cancelada.
     * Pede motivo obrigatorio para registro.
     */
    private void cancelarOportunidade(Usuario solicitante) {
        listarOportunidades();
        System.out.print("ID da oportunidade a cancelar: ");
        int id = lerInt();
        Oportunidade op = oportunidadeService.buscarPorId(id);
        if (op == null) {
            System.out.println("Oportunidade nao encontrada.");
            return;
        }
        if (!(solicitante instanceof Administrador) && !op.getResponsavel().equals(solicitante)) {
            System.out.println("Voce nao e o responsavel por esta oportunidade.");
            return;
        }
        String motivo;
        while (true) {
            System.out.print("Motivo do cancelamento (obrigatorio): ");
            motivo = sc.nextLine().trim();
            if (!motivo.isEmpty()) break;
            System.out.println("Motivo nao pode ser vazio.");
        }
        boolean ok = oportunidadeService.cancelarOportunidade(id);
        if (ok)
            System.out.println("Oportunidade cancelada. Motivo registrado: " + motivo);
        else
            System.out.println("Nao foi possivel cancelar (ja encerrada ou ja cancelada).");
    }

    private void encerrarOportunidade() {
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        Oportunidade o = oportunidadeService.buscarPorId(id);

        if (o == null) {
            System.out.println("Oportunidade nao encontrada.");
            return;
        }

        oportunidadeService.encerrarOportunidade(id);
        System.out.println("Oportunidade encerrada.");

        List<Discente> aprovados = new ArrayList<>(o.getInscritosAprovados());
        if (aprovados.isEmpty()) {
            System.out.println("Nenhum inscrito aprovado para certificar.");
            return;
        }

        System.out.println("\nSelecione quem cumpriu a atividade e deve ser certificado (s = sim, qualquer outra = nao):");
        List<Discente> aCertificar = new ArrayList<>();
        for (Discente d : aprovados) {
            System.out.print("  " + d.getNome() + " - Certificar? (s/n): ");
            String resp = sc.nextLine().trim().toLowerCase();
            if (resp.equals("s")) aCertificar.add(d);
        }

        oportunidadeService.certificarDiscentes(id, aCertificar);
        System.out.println("Certificados gerados para " + aCertificar.size() + " discente(s).");
    }

    private void substituirParticipante(Usuario responsavel) {
        listarOportunidades();
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        Oportunidade op = oportunidadeService.buscarPorId(id);

        if (op == null) {
            System.out.println("Oportunidade nao encontrada.");
            return;
        }
        if (!(responsavel instanceof Administrador) && !op.getResponsavel().equals(responsavel)) {
            System.out.println("Voce nao e o responsavel por esta oportunidade.");
            return;
        }

        List<Discente> aprovados = new ArrayList<>(op.getInscritosAprovados());
        if (aprovados.isEmpty()) {
            System.out.println("Nenhum participante aprovado nesta oportunidade.");
            return;
        }

        System.out.println("-- Participantes aprovados --");
        for (int i = 0; i < aprovados.size(); i++) {
            System.out.println("[" + i + "] " + aprovados.get(i).getNome());
        }
        System.out.print("Selecione quem sera removido: ");
        int idxRemover = lerInt();
        if (idxRemover < 0 || idxRemover >= aprovados.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        Discente aRemover = aprovados.get(idxRemover);

        System.out.print("Justificativa da remocao: ");
        String justificativa = sc.nextLine().trim();

        List<Discente> fila = new ArrayList<>(op.getFilaEspera());
        if (fila.isEmpty()) {
            System.out.println("Fila de espera vazia. Nao ha substituto disponivel.");
            System.out.println(aRemover.getNome() + " foi removido sem substituto. Justificativa: " + justificativa);
            oportunidadeService.cancelarInscricao(id, aRemover);
            return;
        }

        System.out.println("-- Fila de espera (possiveis substitutos) --");
        for (int i = 0; i < fila.size(); i++) {
            System.out.println("[" + i + "] " + fila.get(i).getNome());
        }
        System.out.print("Selecione o substituto: ");
        int idxSubs = lerInt();
        if (idxSubs < 0 || idxSubs >= fila.size()) {
            System.out.println("Indice invalido.");
            return;
        }
        Discente substituto = fila.get(idxSubs);

        boolean ok = oportunidadeService.substituirParticipante(id, aRemover, substituto);
        if (ok) {
            System.out.println("Substituicao realizada: " + aRemover.getNome() +
                               " -> " + substituto.getNome() +
                               " | Justificativa: " + justificativa);
        } else {
            System.out.println("Nao foi possivel realizar a substituicao.");
        }
    }

    private void inscreverEmOportunidade(Discente d) {
        listarOportunidades();
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        boolean ok = oportunidadeService.inscreverDiscente(id, d);
        if (ok)
            System.out.println("Pedido de inscricao enviado com sucesso!");
        else
            System.out.println("Nao foi possivel se inscrever. Verifique se a oportunidade existe, esta ABERTA e sua conta esta ativa.");
    }

    private void cancelarInscricao(Discente d) {
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        boolean ok = oportunidadeService.cancelarInscricao(id, d);
        if (ok)
            System.out.println("Inscricao cancelada com sucesso.");
        else
            System.out.println("Nao foi possivel cancelar. Verifique se a oportunidade existe e esta ABERTA.");
    }

    //Solicitação de Aproveitamento

    private void solicitarAproveitamento(Discente d) {
        List<Certificado> disponiveis = new ArrayList<>();
        for (Certificado c : d.getCertificados()) {
            if (!c.isAproveitamentoSolicitado()) disponiveis.add(c);
        }

        if (disponiveis.isEmpty()) {
            System.out.println("Nenhum certificado disponivel para aproveitamento.");
            System.out.println("(Certificados ja enviados ou voce ainda nao possui certificados)");
            return;
        }

        System.out.println("-- Certificados disponiveis --");
        for (int i = 0; i < disponiveis.size(); i++) {
            System.out.println("[" + i + "] " + disponiveis.get(i));
        }
        System.out.print("Selecione o certificado: ");
        int idx = lerInt();

        if (idx < 0 || idx >= disponiveis.size()) {
            System.out.println("Opcao invalida.");
            return;
        }

        Certificado cert = disponiveis.get(idx);
        SolicitacaoAproveitamento s = new SolicitacaoAproveitamento(d, cert);
        aproveitamentoService.criarSolicitacao(s);
        System.out.println("Solicitacao de aproveitamento enviada para o Coordenador/Comissao.");
    }

    private void verSolicitacoesDiscente(Discente d) {
        boolean encontrou = false;
        String parecerInfo;
        for (SolicitacaoAproveitamento s : aproveitamentoService.listarTodas()) {
            if (s.getSolicitante().equals(d)) {
                if (s.getParecer().isEmpty())
                    parecerInfo = "";
                else
                    parecerInfo = " | Parecer: " + s.getParecer();
                String prazoInfo = "";
                if (s.getStatus() == StatusSolicitacao.INDEFERIDA) {
                    if (s.podeReenviar()) {
                        prazoInfo = " | Reenvio restante: " + s.diasRestantesReenvio() + " dia(s)";
                    } else {
                        prazoInfo = " | Prazo de reenvio expirado";
                    }
                }
                System.out.println(s + parecerInfo + prazoInfo);
                encontrou = true;
            }
        }
        if (!encontrou) {
            System.out.println("Nenhuma solicitacao encontrada.");
        }
    }

    private void reenviarSolicitacao(Discente d) {
        verSolicitacoesDiscente(d);
        System.out.print("ID da solicitacao indeferida: ");
        int id = lerIntMaiorQueZero();

        SolicitacaoAproveitamento s = aproveitamentoService.buscarPorId(id);

        if (s == null || !s.getSolicitante().equals(d)) {
            System.out.println("Solicitacao invalida.");
            return;
        }
        if (s.getStatus() != StatusSolicitacao.INDEFERIDA) {
            System.out.println("Apenas solicitacoes INDEFERIDAS podem ser reenviadas.");
            return;
        }
        if (!s.podeReenviar()) {
            System.out.println("Prazo de " + SolicitacaoAproveitamento.PRAZO_REENVIO_DIAS +
                               " dias para reenvio expirado. Abra uma nova solicitacao.");
            return;
        }

        s.reenviar();
        System.out.println("Solicitacao reenviada com o mesmo certificado: " + s.getCertificado().getTituloAtividade());
        System.out.println("Dias restantes desde indeferimento: " + s.diasRestantesReenvio());
    }

    private void verCertificados(Discente d) {
        if (d.getCertificados().isEmpty()) {
            System.out.println("Nenhum certificado encontrado.");
            return;
        }
        for (Certificado c : d.getCertificados()) {
            System.out.println(c);
        }
    }

    private void listarSolicitacoesPendentes() {
        List<SolicitacaoAproveitamento> lista = aproveitamentoService.listarPendentes();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma solicitacao pendente.");
            return;
        }
        System.out.println("-- Solicitacoes Pendentes --");
        for (SolicitacaoAproveitamento s : lista) {
            System.out.println(s);
        }
    }

    private void listarSolicitacoesPendentesSemDelegacao() {
        List<SolicitacaoAproveitamento> lista = aproveitamentoService.listarPendentesSemDelegacao();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma solicitacao pendente (nao delegada).");
            return;
        }
        System.out.println("-- Solicitacoes Pendentes (nao delegadas) --");
        for (SolicitacaoAproveitamento s : lista) {
            System.out.println(s);
        }
    }

    private void listarSolicitacoesDelegadas() {
        List<SolicitacaoAproveitamento> lista = aproveitamentoService.listarDelegadas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma solicitacao delegada pendente.");
            return;
        }
        System.out.println("-- Solicitacoes Delegadas pela Coordenacao --");
        for (SolicitacaoAproveitamento s : lista) {
            System.out.println(s);
        }
    }

    private void delegarSolicitacaoParaComissao() {
        listarSolicitacoesPendentesSemDelegacao();
        System.out.print("ID da solicitacao a delegar: ");
        int id = lerInt();
        boolean ok = aproveitamentoService.delegarParaComissao(id);
        if (ok)
            System.out.println("Solicitacao delegada para a Comissao com sucesso.");
        else
            System.out.println("Nao foi possivel delegar: solicitacao nao encontrada, nao esta pendente ou ja foi delegada.");
    }

    private void avaliarSolicitacaoDelegada() {
        listarSolicitacoesDelegadas();
        System.out.print("ID da solicitacao: ");
        int id = lerInt();
        SolicitacaoAproveitamento s = aproveitamentoService.buscarPorId(id);

        if (s == null || !s.isDelegadaParaComissao()) {
            System.out.println("Solicitacao nao encontrada ou nao esta delegada para a Comissao.");
            return;
        }

        coletarDecisaoEAvaliar(s);
    }

    private void avaliarSolicitacao() {
        listarSolicitacoesPendentes();
        System.out.print("ID da solicitacao: ");
        int id = lerInt();
        SolicitacaoAproveitamento s = aproveitamentoService.buscarPorId(id);

        if (s == null) {
            System.out.println("Solicitacao nao encontrada.");
            return;
        }

        coletarDecisaoEAvaliar(s);
    }

    /**
     * RF021 - coleta decisao + parecer. Parecer e obrigatorio em caso de indeferimento.
     */
    private void coletarDecisaoEAvaliar(SolicitacaoAproveitamento s) {
        System.out.println("[1] Deferir  [2] Indeferir");
        System.out.print("Opcao: ");
        String op = sc.nextLine().trim();

        if (!op.equals("1") && !op.equals("2")) {
            System.out.println("Opcao invalida. Avaliacao cancelada.");
            return;
        }
        boolean aprovar = op.equals("1");

        String parecer;
        if (aprovar) {
            System.out.print("Parecer (opcional): ");
            parecer = sc.nextLine().trim();
        } else {
            // RF021 - parecer obrigatorio em caso de reprovacao
            while (true) {
                System.out.print("Parecer (OBRIGATORIO para indeferimento): ");
                parecer = sc.nextLine().trim();
                if (!parecer.isEmpty()) break;
                System.out.println("Parecer nao pode ser vazio em caso de indeferimento.");
            }
        }

        aproveitamentoService.avaliarSolicitacao(s, aprovar, parecer);
        System.out.println("Solicitacao avaliada: " + s.getStatus());
    }

    // Grupo Estudantil

    private void solicitarCriacaoGrupo(Discente d) {
        System.out.println("Nome do Grupo: ");
        String nome = sc.nextLine().trim();
        System.out.println("Descrição/Objetivos: ");
        String desc = sc.nextLine().trim();

        List<Docente> docentes = usuarioService.listarDocentes();
        System.out.println("Selecione o docente que aceitou ser o responsavel: ");
        for(int i =0;i<docentes.size();i++){
            System.out.println("[" + i + "]" + docentes.get(i).getNome());
        }
        System.out.println("Opcao: ");
        int idx = lerInt();
        Docente doc = usuarioService.buscarDocentePorIndice(idx);

        if(doc==null){
            System.out.println("Docente nao encontrado.");
            return;
        }

        SolicitacaoGrupoEstudantil s = new SolicitacaoGrupoEstudantil(d,nome,desc,doc);
        grupoService.solicitarCriacaoGrupo(s);
        System.out.println("Solicitacao enviada ao Coordenador/Administrador.");
    }

    private void avaliarSolicitacaoGrupos(){
        List<SolicitacaoGrupoEstudantil> pendentes = grupoService.listarSolicitacoesPendentes();
        if(pendentes.isEmpty()){
            System.out.println("Não ná solicitaç~eos de grupos pendentes");
            return;
        }
        for(SolicitacaoGrupoEstudantil p : pendentes){
            System.out.println(p);
        }

        System.out.println("Id da solicitacao: ");
        int id = lerInt();
        System.out.println("[1] Aprovar(Criar Grupo) [2] Rejeitar");
        int resultado = lerInt();
        if(resultado==1){
            grupoService.avaliarSolicitacaoGrupo(id,true);
        }else if(resultado==2){
            grupoService.avaliarSolicitacaoGrupo(id,false);
        }else{
            System.out.println("Opcao invalido.");
        }
    }

    private void criarGrupo() {
        List<Docente> docentes = usuarioService.listarDocentes();
        if (docentes.isEmpty()) {
            System.out.println("Nenhum docente cadastrado.");
            return;
        }

        System.out.print("Nome do grupo: ");
        String nome = sc.nextLine().trim();

        System.out.println("Selecione o docente responsavel:");
        for (int i = 0; i < docentes.size(); i++) {
            System.out.println("[" + i + "] " + docentes.get(i).getNome());
        }

        System.out.print("Opcao: ");
        int idx = lerInt();
        Docente doc = usuarioService.buscarDocentePorIndice(idx);

        if (doc == null) {
            System.out.println("Docente invalido.");
            return;
        }

        grupoService.criarGrupo(new GrupoEstudantil(nome, doc));
        System.out.println("Grupo criado com sucesso!");
    }

    private boolean listarGrupoPorUsuario(Usuario u){
        List<GrupoEstudantil> lista = grupoService.listarPorUsuario(u);
        if (lista.isEmpty()) {
            System.out.println("Usuário não é responsável por nenhum grupo.");
            return false;
        }
        System.out.println("-- Grupos --");
        for (GrupoEstudantil g : lista) {
            System.out.println(g);
        }
        return true;
    }

    private void listarGrupos() {
        List<GrupoEstudantil> lista = grupoService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum grupo cadastrado.");
            return;
        }
        System.out.println("-- Grupos --");
        for (GrupoEstudantil g : lista) {
            System.out.println(g);
        }
    }

    private void listarGrupo(){
        listarGrupos();
        System.out.println("ID do grupo a ser selecionado: ");
        int id = lerInt();
        GrupoEstudantil g = grupoService.buscarPorId(id);

        if (g == null) {
            System.out.println("Grupo nao encontrado.");
            return;
        }

        List<Discente> membros = g.getMembros();
        List<CargoGrupo> cargos = g.getCargos();
        if (membros.isEmpty()) {
            System.out.println("Nenhum membro cadastrado.");
            return;
        }

        System.out.println("Membros: ");
        for (int i = 0; i < membros.size(); i++) {
            System.out.println("[" + i + "] " + membros.get(i).getNome() + " | Cargo: " + cargos.get(i));
        }


    }

    private void gerenciarGrupo(Usuario u) {
        if (u instanceof Administrador){
            listarGrupos();
        }
        else{
            if(!listarGrupoPorUsuario(u)){
                return;
            }
        }
        System.out.print("ID do grupo: ");
        int id = lerInt();
        GrupoEstudantil g = grupoService.buscarPorId(id);

        if (g == null) {
            System.out.println("Grupo nao encontrado.");
            return;
        }



        System.out.println("[1] Adicionar membro  [2] Remover membro  [3] Definir cargo");
        System.out.print("Opcao: ");
        String op = sc.nextLine().trim();

        List<Discente> discentes = usuarioService.listarDiscentes();
        if (discentes.isEmpty()) {
            System.out.println("Nenhum discente cadastrado no sistema.");
            return;
        }

        System.out.println("Discentes:");
        for (int i = 0; i < discentes.size(); i++) {
            System.out.println("[" + i + "] " + discentes.get(i).getNome());
        }

        System.out.print("Selecione discente: ");
        int idx = lerInt();
        Discente d = usuarioService.buscarDiscentePorIndice(idx);

        if (d == null) {
            System.out.println("Discente invalido.");
            return;
        }

        switch (op) {
            case "1":
                g.adicionarMembro(d);
                System.out.println("Membro adicionado.");
                break;
            case "2":
                g.removerMembro(d);
                System.out.println("Membro removido.");
                break;
            case "3":
                if (!g.isMembro(d)) {
                    System.out.println("Discente nao e membro do grupo estudantil.");
                    break;
                }
                System.out.println("Selecione o cargo:");
                CargoGrupo[] cargosDisponiveis = CargoGrupo.values();
                for (int i = 0; i < cargosDisponiveis.length; i++) {
                    System.out.println("[" + i + "] " + cargosDisponiveis[i]);
                }
                System.out.print("Opcao: ");
                int idxCargo = lerInt();
                if (idxCargo < 0 || idxCargo >= cargosDisponiveis.length) {
                    System.out.println("Cargo invalido.");
                    break;
                }
                g.definirCargo(d, cargosDisponiveis[idxCargo]);
                System.out.println("Cargo definido: " + cargosDisponiveis[idxCargo]);
                break;
            default:
                System.out.println("Opcao invalida.");
                break;
        }
    }

    private void cancelarSolicitacaoAproveitamento(Discente d) {
        verSolicitacoesDiscente(d);
        System.out.print("ID da solicitacao a cancelar: ");
        int id = lerInt();
        boolean ok = aproveitamentoService.cancelarSolicitacao(id, d);
        if (ok)
            System.out.println("Solicitacao cancelada.");
        else
            System.out.println("Nao foi possivel cancelar: solicitacao nao encontrada, nao e sua ou nao esta PENDENTE.");
    }

    private void exibirPainelProgresso(Discente d) {
        Ppc ppc = d.getPpc();
        int totalNecessario = (ppc != null) ? ppc.getHorasExtensaoNecessarias() : CursoService.CARGA_PADRAO;
        int concluidas = d.getHorasCumpridas();
        int horasUce   = oportunidadeService.calcularHorasUceConcluidas(d);
        int pendentes  = oportunidadeService.calcularHorasPendentes(d);

        System.out.println("\n========== PAINEL DE PROGRESSO ==========");
        if (ppc != null) {
            System.out.println("Curso            : " + ppc.getCurso().getCodigo() + " - " + ppc.getCurso().getNome());
            System.out.println("PPC vigente p/voce : " + ppc.getAnoVigencia()
                              + (ppc.isAtiva() ? " (vigente)" : " (historica)"));
        } else {
            System.out.println("PPC              : (nao vinculado - usando carga padrao)");
        }
        System.out.println("Horas concluidas : " + concluidas + "h  (sendo " + horasUce + "h de UCE)");
        System.out.println("Horas pendentes  : " + pendentes  + "h  (inscrito e aprovado, oportunidade ainda em andamento)");
        System.out.println("Total necessario : " + totalNecessario + "h");
        System.out.println();
        System.out.print("Progresso: ");
        exibirBarraProgresso(concluidas, totalNecessario);

        System.out.println("\n-- Meus Certificados --");
        if (d.getCertificados().isEmpty()) {
            System.out.println("  Nenhum certificado ainda.");
        } else {
            for (Certificado c : d.getCertificados()) {
                System.out.println("  " + c);
            }
        }

        System.out.println("\n-- Minhas Solicitacoes de Aproveitamento --");
        boolean found = false;
        for (SolicitacaoAproveitamento s : aproveitamentoService.listarTodas()) {
            if (s.getSolicitante().equals(d)) {
                String parecer = s.getParecer().isEmpty() ? "" : " | Parecer: " + s.getParecer();
                System.out.println("  " + s + parecer);
                found = true;
            }
        }
        if (!found) System.out.println("  Nenhuma solicitacao registrada.");
        System.out.println("=========================================");
    }

    private void exibirBarraProgresso(int atual, int total) {
        final int LARGURA = 30;
        int preenchido = (total > 0) ? Math.min((atual * LARGURA) / total, LARGURA) : 0;
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < LARGURA; i++) {
            barra.append(i < preenchido ? "#" : "-");
        }
        barra.append("]");
        int pct = (total > 0) ? Math.min((atual * 100) / total, 100) : 0;
        System.out.println(barra + " " + atual + "/" + total + "h (" + pct + "%)");
    }

    private void verHistoricoCargos() {
        listarGrupos();
        System.out.print("ID do grupo: ");
        int id = lerInt();
        GrupoEstudantil g = grupoService.buscarPorId(id);

        if (g == null) {
            System.out.println("Grupo nao encontrado.");
            return;
        }

        List<HistoricoCargo> historico = g.getHistoricoCargos();
        if (historico.isEmpty()) {
            System.out.println("Nenhum historico de cargos registrado.");
            return;
        }

        System.out.println("-- Historico de Cargos: " + g.getNome() + " --");
        for (HistoricoCargo h : historico) {
            System.out.println(h);
        }
    }

    private void listarTodosUsuarios() {
        List<Usuario> lista = usuarioService.listarTodos();
        System.out.println("-- Usuarios Cadastrados --");
        for (Usuario u : lista) {
            System.out.println(u);
        }
    }

    // RF007/RF008/RF0009 - Gerenciamento de Cursos, PPCs e UCEs
    private void menuGerenciarCursos() {
        while (true) {
            System.out.println("\n--- GERENCIAR CURSOS / PPCs / UCEs ---");
            System.out.println("[1] Listar cursos");
            System.out.println("[2] Cadastrar curso");
            System.out.println("[3] Editar nome de curso");
            System.out.println("[4] Remover curso");
            System.out.println("[5] Cadastrar nova versao de PPC (RF008)");
            System.out.println("[6] Ver historico de PPCs de um curso");
            System.out.println("[7] Gerenciar UCEs de um PPC (RF0009)");
            System.out.println("[0] Voltar");
            System.out.print("Opcao: ");

            String op = lerOpcao();
            switch (op) {
                case "1": listarCursos(); break;
                case "2": cadastrarCurso(); break;
                case "3": editarNomeCurso(); break;
                case "4": removerCurso(); break;
                case "5": cadastrarNovaVersaoPpc(); break;
                case "6": verHistoricoPpc(); break;
                case "7": menuGerenciarUces(); break;
                case "0": return;
                default: System.out.println("Opcao invalida.");
            }
        }
    }

    private void listarCursos() {
        List<Curso> cursos = cursoService.listarTodos();
        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado.");
            return;
        }
        System.out.println("-- Cursos --");
        for (Curso c : cursos) System.out.println(c);
    }

    private void cadastrarCurso() {
        System.out.print("Codigo do curso (ex: CC, MAT, FIS): ");
        String codigo = sc.nextLine().trim();
        System.out.print("Nome do curso: ");
        String nome = sc.nextLine().trim();

        boolean ok = cursoService.cadastrarCurso(new Curso(codigo, nome));
        if (ok) {
            System.out.println("Curso cadastrado com sucesso! Cadastre agora a primeira versao do PPC dele em [5].");
        } else {
            System.out.println("Codigo de curso ja existente.");
        }
    }

    private void editarNomeCurso() {
        listarCursos();
        System.out.print("ID do curso: ");
        int id = lerInt();
        System.out.print("Novo nome: ");
        String nome = sc.nextLine().trim();
        boolean ok = cursoService.atualizarNome(id, nome);
        System.out.println(ok ? "Nome atualizado." : "Curso nao encontrado ou nome invalido.");
    }

    private void removerCurso() {
        listarCursos();
        System.out.print("ID do curso a remover: ");
        int id = lerInt();
        boolean ok = cursoService.removerCurso(id);
        System.out.println(ok ? "Curso removido." : "Curso nao encontrado.");
    }

    // RF008 - Nova versao do PPC. Quem cadastra fica registrado como autor.
    private void cadastrarNovaVersaoPpc() {
        listarCursos();
        System.out.print("ID do curso: ");
        int idCurso = lerInt();
        Curso c = cursoService.buscarPorId(idCurso);
        if (c == null) { System.out.println("Curso nao encontrado."); return; }

        System.out.print("Ano/versao do PPC (ex: 2025.1): ");
        String ano = sc.nextLine().trim();
        System.out.print("Carga horaria de extensao exigida (horas): ");
        int horas = lerIntMaiorQueZero();

        // Autor da alteracao = usuario logado (capturado em iniciar - aqui usamos null
        // se nao foi possivel, mas o ideal e propagar a referencia)
        Usuario autor = usuarioLogadoAtual;
        boolean ok = cursoService.cadastrarNovaVersaoPpc(idCurso, ano, horas, autor);
        if (ok) System.out.println("Nova versao do PPC cadastrada e marcada como vigente.");
        else    System.out.println("Falha: ano ja existe ou dados invalidos.");
    }

    // RF008 - Historico completo de PPCs de um curso
    private void verHistoricoPpc() {
        listarCursos();
        System.out.print("ID do curso: ");
        int idCurso = lerInt();
        Curso c = cursoService.buscarPorId(idCurso);
        if (c == null) { System.out.println("Curso nao encontrado."); return; }

        List<Ppc> versoes = c.getVersoesPpc();
        if (versoes.isEmpty()) {
            System.out.println("Nenhum PPC cadastrado para este curso.");
            return;
        }
        System.out.println("-- Historico de PPCs de " + c.getCodigo() + " --");
        for (Ppc p : versoes) System.out.println(p);
    }

    // RF0009 - UCEs de um PPC especifico
    private void menuGerenciarUces() {
        listarCursos();
        System.out.print("ID do curso: ");
        int idCurso = lerInt();
        Curso c = cursoService.buscarPorId(idCurso);
        if (c == null) { System.out.println("Curso nao encontrado."); return; }

        List<Ppc> versoes = c.getVersoesPpc();
        if (versoes.isEmpty()) { System.out.println("Curso sem PPC."); return; }

        System.out.println("-- Versoes do PPC --");
        for (int i = 0; i < versoes.size(); i++) {
            System.out.println("[" + i + "] " + versoes.get(i));
        }
        System.out.print("Selecione a versao do PPC: ");
        int idxPpc = lerInt();
        if (idxPpc < 0 || idxPpc >= versoes.size()) {
            System.out.println("Versao invalida.");
            return;
        }
        Ppc ppc = versoes.get(idxPpc);

        while (true) {
            System.out.println("\n--- UCEs DO PPC " + ppc.getCurso().getCodigo() + "/" + ppc.getAnoVigencia() + " ---");
            System.out.println("[1] Listar UCEs");
            System.out.println("[2] Cadastrar UCE");
            System.out.println("[3] Remover UCE");
            System.out.println("[0] Voltar");
            System.out.print("Opcao: ");

            String op = lerOpcao();
            switch (op) {
                case "1":
                    if (ppc.getUces().isEmpty()) {
                        System.out.println("Nenhuma UCE cadastrada.");
                    } else {
                        for (UnidadeCurricular u : ppc.getUces()) System.out.println(u);
                    }
                    break;
                case "2":
                    System.out.print("Codigo (ex: EXT0001): ");
                    String cod = sc.nextLine().trim();
                    System.out.print("Nome: ");
                    String nome = sc.nextLine().trim();
                    System.out.print("Carga horaria: ");
                    int ch = lerIntMaiorQueZero();
                    boolean ok = cursoService.cadastrarUce(ppc, cod, nome, ch);
                    System.out.println(ok ? "UCE cadastrada." : "Codigo duplicado ou dados invalidos.");
                    break;
                case "3":
                    System.out.print("Codigo da UCE a remover: ");
                    String codRm = sc.nextLine().trim();
                    boolean removida = cursoService.removerUce(ppc, codRm);
                    System.out.println(removida ? "UCE removida." : "UCE nao encontrada.");
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

// Métodos de I/O

    // Lê a opção do menu e normaliza zeros à esquerda: "01" -> "1", "09" -> "9"
    private String lerOpcao() {
        String entrada = sc.nextLine().trim();
        try {
            return String.valueOf(Integer.parseInt(entrada));
        } catch (NumberFormatException e) {
            return entrada;
        }
    }

    private int lerInt() {
        while (true) {
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                if (valor >= 0) {
                    return valor;
                } else {
                    System.out.print("Valor inválido. Digite um número maior ou igual a 0: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, digite um número inteiro: ");
            }
        }
    }

    private int lerIntMaiorQueZero() {
        while (true) {
            try {
                int valor = Integer.parseInt(sc.nextLine().trim());
                if (valor > 0) {
                    return valor;
                } else {
                    System.out.print("Valor inválido. Digite um número maior que 0: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, digite um número inteiro: ");
            }
        }
    }
}