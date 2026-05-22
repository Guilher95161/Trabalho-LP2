package interfaceterminal;

import entidades.*;
import entidades.enums.StatusOportunidade;
import servicos.AproveitamentoService;
import servicos.GrupoService;
import servicos.OportunidadeService;
import servicos.UsuarioService;

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
            System.out.println("[2] Cadastrar");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    fazerLogin();
                    break;
                case "2":
                    cadastrarUsuario();
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
            case "GESTOR":
                menuGestor((Gestor) u);
                break;
            case "ADMINISTRADOR":
                menuAdministrador((Administrador) u);
                break;
            default:
                System.out.println("Tipo de usuario desconhecido.");
                break;
        }
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
        System.out.println("Tipo: [1] Discente  [2] Docente  [3] Gestor");
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
                novo = new Gestor(nome, matricula, email, senha);
                break;
            default:
                System.out.println("Tipo invalido.");
                return;
        }

        boolean ok = usuarioService.cadastrarUsuario(novo);
        if (ok)
            System.out.println("Cadastrado realizado com sucesso!");
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
            System.out.println("[04] Solicitar aproveitamento de horas");
            System.out.println("[05] Ver minhas solicitacoes");
            System.out.println("[06] Reenviar solicitacao indeferida");
            System.out.println("[07] Meus certificados");
            System.out.println("[08] Minhas horas cumpridas");
            System.out.println("[09] Propor nova oportunidade de extensão");
            System.out.println("[10] Solicitar criação de grupo estudantil");
            System.out.println("[11] Gerenciar inscritos em minha oportunidade");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = sc.nextLine().trim();

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
                case "0":
                    return;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        }
    }

    private void menuDocente(Docente doc) {
        while (true) {
            System.out.println("\n--- MENU DOCENTE: " + doc.getNome() + " ---");
            System.out.println("[1] Criar oportunidade");
            System.out.println("[2] Ver oportunidades");
            System.out.println("[3] Encerrar oportunidade");
            System.out.println("[4] Ver grupos");
            System.out.println("[5] Gerenciar membros de grupo");
            System.out.println("[6] Gerenciar membros de grupo");
            System.out.println("[7] Avaliar inscricoes pendentes em minhas oportunidades");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    criarOportunidade(doc, StatusOportunidade.ABERTA);
                    break;
                case "2":
                    listarOportunidades();
                    break;
                case "3":
                    encerrarOportunidade();
                    break;
                case "4":
                    listarGrupos();
                    break;
                case "5":
                    listarGrupo();
                    break;
                case "6":
                    gerenciarGrupo(doc);
                    break;
                case "7":
                    avaliarInscricoesOportunidade(doc);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void menuGestor(Gestor g) {
        while (true) {
            System.out.println("\n--- MENU GESTOR: " + g.getNome() + " ---");
            System.out.println("[1] Ver solicitacoes pendentes");
            System.out.println("[2] Avaliar solicitacao de horas cumpridas");
            System.out.println("[3] Ver solicitacoes de criação de grupos");
            System.out.println("[4] Ver grupos");
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    listarSolicitacoesPendentes();
                    break;
                case "2":
                    avaliarSolicitacao();
                    break;
                case "3":
                    avaliarSolicitacaoGrupos();
                    break;
                case "4":
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

    private void menuAdministrador(Administrador a) {
        while (true) {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("[01] Cadastrar Usuário");
            System.out.println("[02] Listar todos os usuarios");
            System.out.println("[03] Desativar Usuario");
            System.out.println("[04] Avaliar solicitacao de criação de grupos");
            System.out.println("[05] Criar oportunidade");
            System.out.println("[06] Ver oportunidades");
            System.out.println("[07] Encerrar oportunidade");
            System.out.println("[08] Ver solicitacoes pendentes");
            System.out.println("[09] Avaliar solicitacao");
            System.out.println("[10] Criar grupo estudantil");
            System.out.println("[11] Ver grupos");
            System.out.println("[12] Ver grupo");
            System.out.println("[13] Gerenciar membros de grupo");
            System.out.println("[00] Sair");
            System.out.print("Opcao: ");

            String op = sc.nextLine().trim();

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
                    encerrarOportunidade();
                    break;
                case "8":
                    listarSolicitacoesPendentes();
                    break;
                case "9":
                    avaliarSolicitacao();
                    break;
                case "10":
                    criarGrupo();
                    break;
                case "11":
                    listarGrupos();
                    break;
                case "12":
                    listarGrupo();
                    break;
                case "13":
                    gerenciarGrupo(a);
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
        System.out.println("--- Propor Oportunidade ---");
        criarOportunidade(d,StatusOportunidade.AGUARDANDO_APROVACAO);
        System.out.println("A sua proposta foi enviada para aprovação docente");
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
        System.out.print("Carga horaria: ");
        int ch = lerIntMaiorQueZero();
        System.out.print("Vagas: ");
        int vagas = lerInt();

        oportunidadeService.criarOportunidade(new Oportunidade(titulo, ch, vagas, responsavel, status));
        if (status == StatusOportunidade.ABERTA){
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

        System.out.print("Titulo: ");
        String titulo = sc.nextLine().trim();
        System.out.print("Carga horaria: ");
        int ch = lerIntMaiorQueZero();
        System.out.print("Vagas: ");
        int vagas = lerInt();

        criarOportunidade(doc,StatusOportunidade.ABERTA);
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
        System.out.println("Oportunidade encerrada. Certificados gerados para " + o.getInscritosAprovados().size() + " inscritos.");
    }

    private void inscreverEmOportunidade(Discente d) {
        listarOportunidades();
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        oportunidadeService.inscreverDiscente(id, d);
        System.out.println("Pedido de inscrição enviado com sucesso!");
    }

    private void cancelarInscricao(Discente d) {
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        oportunidadeService.cancelarInscricao(id, d);
        System.out.println("Inscricao cancelada.");
    }

    //Solicitação de Aproveitamento

    private void solicitarAproveitamento(Discente d) {
        System.out.print("Descricao do curso/atividade: ");
        String desc = sc.nextLine().trim();
        System.out.print("Carga horaria pleiteada: ");
        int ch = lerIntMaiorQueZero();

        SolicitacaoAproveitamento s = new SolicitacaoAproveitamento(d, desc, ch);
        aproveitamentoService.criarSolicitacao(s);
        System.out.println("Solicitacao registrada com status PENDENTE.");
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
        if (!s.getStatus().equals("INDEFERIDA")) {
            System.out.println("Apenas solicitacoes INDEFERIDAS podem ser reenviadas.");
            return;
        }

        System.out.print("Nova descricao: ");
        s.setDescricaoCurso(sc.nextLine().trim());
        System.out.print("Nova carga horaria: ");
        s.setCargaHorariaPleitada(lerInt());
        s.reenviar();
        System.out.println("Solicitacao reenviada como PENDENTE.");
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
        System.out.println("Solicitacao enviada ao gestor/adm");
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
        List<String> cargos = g.getCargos();
        if (membros.isEmpty() || cargos.isEmpty()) {
            System.out.println("Nenhum membro cadastrado.");
            return;
        }

        System.out.println("Membros: ");
        for (int i = 0; i < membros.size(); i++) {
            System.out.println("[" + i + "] " + membros.get(i).getNome() + " Cargo: " + cargos.get(i));
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
                if(g.getCargo(d).equals("NAO_MEMBRO")){
                    System.out.println("Discente não é membro do grupo estudantil");
                    break;
                }
                System.out.print("Cargo (ex: DIRETOR, TESOUREIRO): ");
                String cargo = sc.nextLine().trim();
                g.definirCargo(d, cargo);
                System.out.println("Cargo definido com sucesso.");
                break;
            default:
                System.out.println("Opcao invalida.");
                break;
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