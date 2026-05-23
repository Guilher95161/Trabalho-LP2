package interfaceterminal;

import entidades.*;
import entidades.enums.CargoGrupo;
import entidades.enums.ModalidadeOportunidade;
import entidades.enums.StatusOportunidade;
import entidades.enums.StatusSolicitacao;
import servicos.AproveitamentoService;
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
    private final Scanner sc;

    public MenuTerminal(UsuarioService usuarioService, OportunidadeService oportunidadeService, AproveitamentoService aproveitamentoService, GrupoService grupoService) {
        this.usuarioService = usuarioService;
        this.oportunidadeService = oportunidadeService;
        this.aproveitamentoService = aproveitamentoService;
        this.grupoService = grupoService;
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

        Usuario novo;
        novo = new Discente(nome, matricula, email, senha);

        boolean ok = usuarioService.cadastrarUsuario(novo);
        if (ok)
            System.out.println("Cadastro realizado com sucesso!");
        else
            System.out.println("Email já cadastrado.");
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
                novo = new Discente(nome, matricula, email, senha);
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
            System.out.println("[4] Encerrar oportunidade");
            System.out.println("[5] Ver grupos");
            System.out.println("[6] Ver membros de grupo");
            System.out.println("[7] Gerenciar membros de grupo");
            System.out.println("[8] Ver historico de cargos de um grupo");
            System.out.println("[9] Avaliar inscricoes pendentes em minhas oportunidades");
            System.out.println("[10] Substituir participante");
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
                    encerrarOportunidade();
                    break;
                case "5":
                    listarGrupos();
                    break;
                case "6":
                    listarGrupo();
                    break;
                case "7":
                    gerenciarGrupo(doc);
                    break;
                case "8":
                    verHistoricoCargos();
                    break;
                case "9":
                    avaliarInscricoesOportunidade(doc);
                    break;
                case "10":
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
            System.out.println("[08] Encerrar oportunidade");
            System.out.println("[09] Ver solicitacoes pendentes de aproveitamento");
            System.out.println("[10] Avaliar solicitacao de aproveitamento");
            System.out.println("[11] Criar grupo estudantil");
            System.out.println("[12] Ver grupos");
            System.out.println("[13] Ver grupo");
            System.out.println("[14] Gerenciar membros de grupo");
            System.out.println("[15] Ver historico de cargos de um grupo");
            System.out.println("[16] Substituir participante em oportunidade");
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
                    encerrarOportunidade();
                    break;
                case "9":
                    listarSolicitacoesPendentes();
                    break;
                case "10":
                    avaliarSolicitacao();
                    break;
                case "11":
                    criarGrupo();
                    break;
                case "12":
                    listarGrupos();
                    break;
                case "13":
                    listarGrupo();
                    break;
                case "14":
                    gerenciarGrupo(a);
                    break;
                case "15":
                    verHistoricoCargos();
                    break;
                case "16":
                    substituirParticipante(a);
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

        oportunidadeService.criarOportunidade(
            new Oportunidade(titulo, descricao, modalidade, periodo, ch, vagas, responsavel, status)
        );
        if (status == StatusOportunidade.ABERTA) {
            System.out.println("Oportunidade aberta com sucesso!");
        }
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
                    parecerInfo = "| Parecer: " + s.getParecer();
                System.out.println(s + parecerInfo);
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

        s.reenviar();
        System.out.println("Solicitacao reenviada com o mesmo certificado: " + s.getCertificado().getTituloAtividade());
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

        System.out.println("[1] Deferir  [2] Indeferir");
        System.out.print("Opcao: ");
        String op = sc.nextLine().trim();

        System.out.print("Parecer: ");
        String parecer = sc.nextLine().trim();

        aproveitamentoService.avaliarSolicitacao(s, op.equals("1"), parecer);
        System.out.println("Solicitacao avaliada: " + s.getStatus());
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

        System.out.println("[1] Deferir  [2] Indeferir");
        System.out.print("Opcao: ");
        String op = sc.nextLine().trim();

        System.out.print("Parecer: ");
        String parecer = sc.nextLine().trim();

        aproveitamentoService.avaliarSolicitacao(s, op.equals("1"), parecer);
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
        final int TOTAL_NECESSARIO = 310;
        int concluidas = d.getHorasCumpridas();
        int pendentes  = oportunidadeService.calcularHorasPendentes(d);

        System.out.println("\n========== PAINEL DE PROGRESSO ==========");
        System.out.println("Horas concluidas : " + concluidas + "h");
        System.out.println("Horas pendentes  : " + pendentes  + "h  (inscrito e aprovado, oportunidade ainda aberta)");
        System.out.println("Total necessario : " + TOTAL_NECESSARIO + "h");
        System.out.println();
        System.out.print("Progresso: ");
        exibirBarraProgresso(concluidas, TOTAL_NECESSARIO);

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