package interfaceterminal;

import entidades.*;
import servicos.GerenciadorCentral;

import java.util.List;
import java.util.Scanner;

public class MenuTerminal {

    private GerenciadorCentral gerenciador;
    private Scanner sc;

    public MenuTerminal(GerenciadorCentral gerenciador) {
        this.gerenciador = gerenciador;
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
                    return; // Sai do método, encerrando o loop principal
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

        Usuario u = gerenciador.buscarPorEmailSenha(email, senha);

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

        boolean ok = gerenciador.cadastrarUsuario(novo);
        if(ok)
            System.out.println("Cadastrado realizado com sucesso!");
        else
            System.out.println("Email já cadastrado.");
    }

    // Menus

    private void menuDiscente(Discente d) {
        while (true) {
            System.out.println("\n--- MENU DISCENTE: " + d.getNome() + " ---");
            System.out.println("[1] Ver oportunidades abertas");
            System.out.println("[2] Inscrever-se em oportunidade");
            System.out.println("[3] Cancelar inscricao");
            System.out.println("[4] Solicitar aproveitamento de horas");
            System.out.println("[5] Ver minhas solicitacoes");
            System.out.println("[6] Reenviar solicitacao indeferida");
            System.out.println("[7] Meus certificados");
            System.out.println("[8] Minhas horas cumpridas");
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
            System.out.println("[0] Sair");
            System.out.print("Opcao: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    criarOportunidade(doc);
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
                    gerenciarGrupo();
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
            System.out.println("[2] Avaliar solicitacao");
            System.out.println("[3] Criar grupo estudantil");
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
                    criarGrupo();
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
            System.out.println("[1] Cadastrar Usuário");
            System.out.println("[2] Listar todos os usuarios");
            System.out.println("[3] Criar oportunidade");
            System.out.println("[4] Ver oportunidades");
            System.out.println("[5] Encerrar oportunidade");
            System.out.println("[6] Ver solicitacoes pendentes");
            System.out.println("[7] Avaliar solicitacao");
            System.out.println("[8] Criar grupo estudantil");
            System.out.println("[9] Ver grupos");
            System.out.println("[10] Gerenciar membros de grupo");
            System.out.println("[0] Sair");
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
                    criarOportunidadeAdmin();
                    break;
                case "4":
                    listarOportunidades();
                    break;
                case "5":
                    encerrarOportunidade();
                    break;
                case "6":
                    listarSolicitacoesPendentes();
                    break;
                case "7":
                    avaliarSolicitacao();
                    break;
                case "8":
                    criarGrupo();
                    break;
                case "9":
                    listarGrupos();
                    break;
                case "10":
                    gerenciarGrupo();
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

    private void listarOportunidades() {
        List<Oportunidade> lista = gerenciador.listarOportunidades();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma oportunidade cadastrada.");
            return;
        }
        System.out.println("-- Oportunidades --");
        for (Oportunidade o : lista) {
            System.out.println(o);
        }
    }

    private void criarOportunidade(Docente doc) {
        System.out.print("Titulo: ");
        String titulo = sc.nextLine().trim();
        System.out.print("Carga horaria: ");
        int ch = lerInt();
        System.out.print("Vagas: ");
        int vagas = lerInt();

        gerenciador.criarOportunidade(new Oportunidade(titulo, ch, vagas, doc));
        System.out.println("Oportunidade criada com sucesso!");
    }

    private void criarOportunidadeAdmin() {
        List<Docente> docentes = gerenciador.listarDocentes();
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
        Docente doc = gerenciador.buscarDocentePorId(idx);

        if (doc == null) {
            System.out.println("Docente invalido.");
            return;
        }

        criarOportunidade(doc);
    }

    private void encerrarOportunidade() {
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        Oportunidade o = gerenciador.buscarOportunidadePorId(id);

        if (o == null) {
            System.out.println("Oportunidade nao encontrada.");
            return;
        }

        gerenciador.encerrarOportunidade(id);
        System.out.println("Oportunidade encerrada. Certificados gerados para " + o.getInscritos().size() + " inscritos.");
    }

    private void inscreverEmOportunidade(Discente d) {
        listarOportunidades();
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        gerenciador.inscreverDiscente(id, d);
        System.out.println("Inscricao realizada com sucesso!");
    }

    private void cancelarInscricao(Discente d) {
        System.out.print("ID da oportunidade: ");
        int id = lerInt();
        gerenciador.cancelarInscricao(id, d);
        System.out.println("Inscricao cancelada.");
    }

    private void solicitarAproveitamento(Discente d) {
        System.out.print("Descricao do curso/atividade: ");
        String desc = sc.nextLine().trim();
        System.out.print("Carga horaria pleiteada: ");
        int ch = lerInt();

        SolicitacaoAproveitamento s = new SolicitacaoAproveitamento(d, desc, ch);
        gerenciador.criarSolicitacao(s);
        System.out.println("Solicitacao registrada com status PENDENTE.");
    }

    private void verSolicitacoesDiscente(Discente d) {
        boolean encontrou = false;
        String parecerInfo;
        for (SolicitacaoAproveitamento s : gerenciador.listarSolicitacoes()) {
            if (s.getSolicitante().equals(d)) {
                if(s.getParecer().isEmpty())
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
        int id = lerInt();

        SolicitacaoAproveitamento s = gerenciador.buscarSolicitacaoPorId(id);

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
        List<SolicitacaoAproveitamento> lista = gerenciador.listarSolicitacoesPendentes();
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
        SolicitacaoAproveitamento s = gerenciador.buscarSolicitacaoPorId(id);

        if (s == null) {
            System.out.println("Solicitacao nao encontrada.");
            return;
        }

        System.out.println("[1] Deferir  [2] Indeferir");
        System.out.print("Opcao: ");
        String op = sc.nextLine().trim();

        System.out.print("Parecer: ");
        String parecer = sc.nextLine().trim();

        gerenciador.avaliarSolicitacao(s, op.equals("1"), parecer);
        System.out.println("Solicitacao avaliada: " + s.getStatus());
    }

    private void criarGrupo() {
        List<Docente> docentes = gerenciador.listarDocentes();
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
        Docente doc = gerenciador.buscarDocentePorId(idx);

        if (doc == null) {
            System.out.println("Docente invalido.");
            return;
        }

        gerenciador.criarGrupo(new GrupoEstudantil(nome, doc));
        System.out.println("Grupo criado com sucesso!");
    }

    private void listarGrupos() {
        List<GrupoEstudantil> lista = gerenciador.listarGrupos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum grupo cadastrado.");
            return;
        }
        System.out.println("-- Grupos --");
        for (GrupoEstudantil g : lista) {
            System.out.println(g);
        }
    }

    private void gerenciarGrupo() {
        listarGrupos();
        System.out.print("ID do grupo: ");
        int id = lerInt();
        GrupoEstudantil g = gerenciador.buscarGrupoPorId(id);

        if (g == null) {
            System.out.println("Grupo nao encontrado.");
            return;
        }

        System.out.println("[1] Adicionar membro  [2] Remover membro  [3] Definir cargo");
        System.out.print("Opcao: ");
        String op = sc.nextLine().trim();

        List<Discente> discentes = gerenciador.listarDiscentes();
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
        Discente d = gerenciador.buscarDiscentePorId(idx);

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
                System.out.print("Cargo (ex: DIRETOR, TESOUREIRO): ");
                String cargo = sc.nextLine().trim();
                if(g.getCargo(d).equals("NAO_MEMBRO")){
                    System.out.println("Discente não pertence ao grupo");
                    break;
                }
                g.definirCargo(d, cargo);
                System.out.println("Cargo definido com sucesso.");
                break;
            default:
                System.out.println("Opcao invalida.");
                break;
        }
    }

    private void listarTodosUsuarios() {
        List<Usuario> lista = gerenciador.listarUsuarios();
        System.out.println("-- Usuarios Cadastrados --");
        for (Usuario u : lista) {
            System.out.println(u);
        }
    }

    // Métodos de I/O

    private int lerInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}