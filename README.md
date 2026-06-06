# Sistema de Extensão UFMA

Projeto em Java desenvolvido para a disciplina de LP2. A aplicação simula, via terminal, um sistema de extensão universitária com diferentes perfis de acesso e operações de gerenciamento acadêmico.

## Objetivo

O sistema tem como objetivo centralizar o gerenciamento de:

* usuários com diferentes papéis
* oportunidades de extensão
* solicitações de aproveitamento de horas
* grupos estudantis
* certificados emitidos ao encerrar oportunidades
* solicitações de aproveitamento de horas (com delegação e prazos)
* grupos estudantis com histórico de cargos

## Perfis de usuário

### Discente

* Auto-cadastro vinculando-se a um curso e PPC específico
* Visualizar e inscrever-se em oportunidades abertas
* Cancelar inscrição
* Solicitar aproveitamento de horas a partir de um certificado recebido
* Reenviar solicitação indeferida dentro do prazo de 5 dias
* Cancelar a própria solicitação de aproveitamento
* Propor oportunidade de extensão (apenas se for líder de algum grupo estudantil)
* Solicitar a criação de um novo grupo estudantil
* Gerenciar inscritos quando é responsável pela oportunidade
* Acompanhar progresso de horas em um painel visual
* Salvar oportunidades como rascunho, editar e submeter depois

### Docente

* Criar oportunidades de extensão (já abertas) informando o componente curricular (UCE)
* Aprovar propostas de oportunidades enviadas por discentes líderes
* Iniciar a execução de uma oportunidade aberta
* Encerrar oportunidade e escolher seletivamente quem será certificado
* Cancelar oportunidade (com motivo obrigatório)
* Substituir participantes com justificativa
* Atribuir e remover cargos em grupos estudantis sob sua responsabilidade
* Consultar histórico de cargos
* Avaliar inscrições pendentes nas suas oportunidades
* Trabalhar com rascunhos antes de publicar

### Coordenador

* Avaliar solicitações de aproveitamento (deferir/indeferir com parecer)
* Delegar solicitações para a Comissão
* Aprovar pedidos de criação de grupos estudantis
* Criar grupos estudantis diretamente
* Criar oportunidades de extensão
* Cadastrar e versionar o PPC do curso (preservando histórico)
* Gerenciar as UCEs de cada versão do PPC

### Comissão

* Avaliar solicitações de aproveitamento delegadas pelo Coordenador

### Administrador

* Acesso completo: pode executar qualquer ação dos outros perfis
* Cadastrar usuários de qualquer tipo
* Desativar contas (preservando histórico para auditoria)
* Único perfil que pode operar sem restrições de responsabilidade

## Funcionalidades em destaque

* **Versionamento de PPC** — ao atualizar a carga horária, uma nova versão é criada e a anterior fica como histórica.
* **UCEs por PPC** — cada versão do PPC tem suas próprias unidades curriculares de extensão, suportando turmas de PPCs diferentes (ex: 2020 e 2025) ao mesmo tempo.
* **Painel de progresso** — lista os certificados já aproveitados (com suas horas) e exibe uma barra textual comparando o total acumulado à carga exigida pelo PPC do aluno.
* **Fluxo unificado** — inscrição → aprovação → execução → encerramento → certificação seletiva → aproveitamento → horas computadas.
* **Prazos** — 10 dias para o Coordenador avaliar; 5 dias para o discente reenviar uma solicitação indeferida.
* **Delegação** — Coordenador pode passar uma solicitação para a Comissão; cada um vê só o que é seu.
* **Rascunhos** — qualquer perfil que cria oportunidades pode salvar como rascunho, editar e submeter quando estiver pronto.
* **Tratamento de exceções** — hierarquia de exceções de domínio em `br.ufma.excecao` (raiz: `SistemaExtensaoException`). Os serviços lançam erros de negócio tipados (`EntidadeNaoEncontradaException`, `OperacaoInvalidaException`, `EmailJaCadastradoException`, `UsuarioInativoException`) e o menu captura no ponto certo, mantendo a UX consistente.

## Tecnologias e características

* **Spring Boot 4.0.6** sobre **Java 21**, build com **Maven** (wrapper `mvnw`) — *etapa 3 em andamento*
* Banco **H2 em memória** (console em `/h2-console`); **entidades já mapeadas em JPA** (herança JOINED, `@GeneratedValue(IDENTITY)`) e **8 repositórios `JpaRepository`** criados — falta ligar os services a eles
* Domínio em Java puro herdado da etapa 2 (regras, máquinas de estado, hierarquia de exceções `unchecked`), agora anotado com JPA
* **Store ativo ainda em memória** (`RepositorioCentral` com `LinkedHashMap`/`LinkedHashSet`): services e CLI continuam usando-o; a troca pelos `JpaRepository` é o cutover dos próximos passos
* Interface funcional hoje é o **menu de terminal** (`Main` + `MenuTerminal`); os endpoints REST entram quando a camada controller for criada

## Requisitos

* **JDK 21** (o projeto roda em Java 21; aponte o `JAVA_HOME` para um JDK 21 ao buildar).
* Não precisa instalar Maven — o **Maven Wrapper** (`mvnw`) baixa tudo. Internet na primeira execução.

## Usuários iniciais para teste

O sistema já inicia com alguns usuários cadastrados:

| Papel         | Email                | Senha       |
| ------------- | -------------------- | ----------- |
| Administrador | `admin@ufma.br`      | `admin123`  |
| Discente      | `aluno1@ufma.br`     | `aluno123`  |
| Discente      | `aluno2@ufma.br`     | `aluno123`  |
| Coordenador   | `coord1@ufma.br`     | `coord123`  |
| Comissão      | `comissao1@ufma.br`  | `com123`    |
| Docente       | `doc@ufma.br`        | `doc123`    |

Os dois discentes já vêm vinculados ao PPC **CC/2020** (310h, com duas UCEs cadastradas).

## Como executar

> **Java 21:** se a máquina tiver mais de um JDK, garanta que o build use o 21 (via `JAVA_HOME`).

### IntelliJ IDEA

1. Abra a pasta do projeto (o IntelliJ reconhece o `pom.xml` como projeto **Maven**).
2. Configure o **Project SDK = 21**.
3. Rode `ExtensaoApplication` (app web em `http://localhost:8080`) — ou `br.ufma.Main` para o menu de terminal legado.

### PowerShell (Windows)

No diretório raiz do projeto:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"   # ajuste para o caminho do seu JDK 21
.\mvnw.cmd spring-boot:run
# abre http://localhost:8080/h2-console
```

### Linux/macOS

No diretório raiz do projeto:

```bash
export JAVA_HOME=/caminho/para/jdk-21
./mvnw spring-boot:run
```

## Estrutura do projeto

```text
src/main/java/br/ufma/
|-- ExtensaoApplication.java   (entry Spring)
|-- Main.java                  (entry do menu CLI legado)
|-- model/
|   |-- entidades/  (+ enums/)
|   `-- repositorio/
|-- servico/
|-- excecao/
`-- interfaceterminal/
src/main/resources/application.properties   (H2)
pom.xml · mvnw · .mvn/   (build Maven)
```

* `ExtensaoApplication`: ponto de entrada Spring Boot (`@SpringBootApplication`).
* `Main`: entry do menu de terminal legado — instancia os serviços, popula cenários de demonstração e abre o menu.
* `model/entidades` (+ `enums`): classes de domínio (Usuario, Oportunidade, Curso, Ppc, UnidadeCurricular, GrupoEstudantil, MembroGrupo, Certificado…) e enums de status/classificação — **anotadas com JPA** (`@Entity`, herança JOINED, relações).
* `model/repositorio`: `RepositorioCentral` (store em memória, ainda ativo) **+ 8 interfaces `JpaRepository`** (já criadas, ainda não ligadas aos services).
* `servico`: regras de negócio e orquestração entre entidades.
* `excecao`: hierarquia de exceções de domínio (`SistemaExtensaoException` e filhas).
* `interfaceterminal`: menus e interação com o usuário (legado, será substituído por REST).

---

## Cenários pré-populados

Ao iniciar, o sistema já vem com sete cenários prontos cobrindo o fluxo principal e suas variações. Cada um para em um ponto diferente da máquina de estados, permitindo testar qualquer etapa sem precisar refazer as anteriores.

| # | Oportunidade | Status | Para quê serve |
|---|---|---|---|
| 1 | Curso de Java — Fundamentos (UCE EXT0001, 40h) | ENCERRADA, aproveitamento DEFERIDO | `aluno1` já tem 40h aproveitadas no painel |
| 2 | Minicurso de Git (20h) | EM_EXECUCAO, `aluno1` aprovado | Demonstrar **encerrar + certificar** ao vivo |
| 3 | Workshop de Algoritmos (30h) | ABERTA, sem inscritos | Demonstrar **inscrição** ao vivo |
| 4 | Palestra: Ética em IA (4h) | ENCERRADA, `aluno2` certificado | Demonstrar `aluno2` **solicitando aproveitamento** ao vivo |
| 5 | Seminário de Sistemas Distribuídos (16h) | Aproveitamento PENDENTE | Demonstrar `coord1` **avaliando** ao vivo |
| 6 | Curso de Python Avançado (24h) | Aproveitamento DELEGADO à Comissão | Demonstrar `comissao1` **avaliando** ao vivo |
| 7 | Workshop de Banco de Dados (12h) | INDEFERIDO há 10 dias | Demonstrar **prazo de reenvio (5d) estourado** |

Além disso, o curso CC já vem com **duas versões de PPC** (2020 e 2025) e um **grupo estudantil** ("Liga Acadêmica de Computação") com `aluno1` como presidente, habilitando a opção *Propor oportunidade* no menu dele.

---

## Exemplo de fluxo de teste

Para validar o fluxo unificado do sistema (inscrição → aproveitamento) **do zero**, siga este roteiro:

1. **Criar oportunidade (Docente):**
   * Login com `doc@ufma.br` / `doc123`.
   * Opção **[1] Criar oportunidade**.
   * Quando perguntado "Salvar como rascunho?", responda `n` para publicar direto.
   * Preencha (ex.: Título "Curso de Java", Modalidade `CURSO`, CH `40`, Vagas `5`).
   * Escolha o componente curricular: opção **[1]** para vincular a uma UCE concreta (ex.: `EXT0001` do PPC CC/2020) ou **[2]** para descrever em texto livre.
   * Saia do menu.

2. **Inscrição (Discente):**
   * Login com `aluno1@ufma.br` / `aluno123`.
   * Opção **[2] Inscrever-se em oportunidade** e digite o ID da oportunidade criada.
   * Saia.

3. **Aprovação da inscrição e execução (Docente):**
   * Volte como `doc@ufma.br`.
   * Opção **[11] Avaliar inscricoes pendentes em minhas oportunidades**.
   * Aprove o discente.
   * Opção **[4] Iniciar execucao** (status muda para `EM_EXECUCAO`).
   * Opção **[5] Encerrar oportunidade**, marque o aluno com `s` quando perguntado se ele deve ser certificado.

4. **Solicitar aproveitamento (Discente):**
   * Volte como `aluno1@ufma.br`.
   * Opção **[7] Meus certificados** — confirme que o certificado foi emitido.
   * Opção **[4] Solicitar aproveitamento de certificado** e escolha o certificado recém-emitido.

5. **Deferimento (Coordenador):**
   * Login com `coord1@ufma.br` / `coord123`.
   * Opção **[1] Ver solicitacoes pendentes** e em seguida **[2] Avaliar solicitacao**.
   * Escolha **deferir**. Em caso de indeferimento, o sistema exige parecer obrigatório.

6. **Verificação (Discente):**
   * Volte como `aluno1@ufma.br`.
   * Opção **[13] Painel de progresso de horas** — o certificado aparece na lista "Certificados Aproveitados" e a barra reflete as horas computadas, comparadas à carga exigida pelo PPC do aluno.

### Outros fluxos para explorar

* **Versionar PPC** (Coordenador, opção **[7] Gerenciar Cursos (PPC)** → cadastrar nova versão).
* **Solicitar criação de grupo** (Discente, opção **[10]**) e aprovar como Coordenador/Admin.
* **Rascunho de oportunidade** — salve como rascunho, edite (opção dedicada nos menus) e submeta depois.
* **Delegar para a Comissão** — Coordenador delega uma solicitação e a Comissão (`comissao1@ufma.br`) avalia.
* **Desativar usuário** (Admin, opção **[3]**) — depois disso ele não consegue mais logar nem ser vinculado a novos grupos/projetos.

---

## Limitações atuais

* Persistência ainda em memória — reiniciar apaga tudo. As entidades já estão mapeadas em JPA e há repositórios `JpaRepository`, mas os services ainda gravam no `RepositorioCentral`; o banco H2/JPA só vira o store ativo após o cutover dos services.
* A interface funcional ainda é o terminal; os endpoints REST entram quando a camada controller for criada
* `Oportunidade.periodoRealizacao` continua texto livre (string), não `LocalDate` — checagens de prazo de inscrição por data ainda não são feitas. (`Certificado` e `HistoricoCargo` já usam `LocalDate`.)
* Não há sistema de notificações ou alertas automáticos por tempo
