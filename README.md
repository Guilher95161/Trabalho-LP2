# Sistema de Extensão UFMA

Projeto em Java desenvolvido para a disciplina de LP2. A aplicação simula, via terminal, um sistema de extensão universitária com diferentes perfis de acesso e operações de gerenciamento acadêmico.

## Objetivo

O sistema tem como objetivo centralizar o gerenciamento de:

* usuários com diferentes papéis
* cursos com versionamento de PPC e UCEs
* oportunidades de extensão (com fluxo completo de aprovação, execução e encerramento)
* inscrições e substituição de participantes
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

* Criar oportunidades (já abertas) e marcá-las como UCE
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
* **Painel de progresso** — barra textual com horas concluídas, horas pendentes (já inscritas) e total exigido pelo PPC do aluno.
* **Fluxo unificado** — inscrição → aprovação → execução → encerramento → certificação seletiva → aproveitamento → horas computadas.
* **Prazos** — 10 dias para o Coordenador avaliar; 5 dias para o discente reenviar uma solicitação indeferida.
* **Delegação** — Coordenador pode passar uma solicitação para a Comissão; cada um vê só o que é seu.
* **Rascunhos** — qualquer perfil que cria oportunidades pode salvar como rascunho, editar e submeter quando estiver pronto.

## Tecnologias e características

* Java puro (sem frameworks ou bibliotecas externas)
* Aplicação de console
* Armazenamento em memória usando `LinkedHashMap` e `LinkedHashSet` para garantir busca eficiente e ordem de inserção
* Sem banco de dados ou persistência em arquivos

## Requisitos

* O projeto foi pensado no JDK 21, mas funciona a partir do JDK 8.
* Terminal com suporte aos comandos `javac` e `java`

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

### IntelliJ IDEA

1. Abra a pasta do projeto no IntelliJ IDEA.
2. Configure o SDK do Java, se necessário.
3. Execute a classe `Main` localizada em `src/Main.java`.

### PowerShell

No diretório raiz do projeto:

```powershell
if (-not (Test-Path out)) { New-Item -ItemType Directory -Path out | Out-Null }
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp out Main
```

### CMD

No diretório raiz do projeto:

```bat
if not exist out mkdir out
javac -d out src\Main.java src\entidades\*.java src\entidades\enums\*.java src\servicos\*.java src\repositorio\*.java src\interfaceterminal\*.java
java -cp out Main
```

### Linux/macOS

No diretório raiz do projeto:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out Main
```

## Estrutura do projeto

```text
src/
|-- Main.java
|-- entidades/
|   `-- enums/
|-- interfaceterminal/
|-- repositorio/
`-- servicos/
```

* `src/Main.java`: ponto de entrada da aplicação
* `src/entidades`: classes de domínio (Usuario, Oportunidade, Curso, Ppc, UnidadeCurricular, GrupoEstudantil, Certificado, etc.)
* `src/entidades/enums`: enums de status e classificação (StatusOportunidade, StatusSolicitacao, CargoGrupo, ModalidadeOportunidade)
* `src/interfaceterminal`: menus e interação com o usuário
* `src/repositorio`: armazenamento central em memória
* `src/servicos`: regras de negócio e orquestração entre entidades

---

## Exemplo de fluxo de teste

Para validar o fluxo unificado do sistema (inscrição → aproveitamento), siga este roteiro:

1. **Criar oportunidade (Docente):**
   * Login com `doc@ufma.br` / `doc123`.
   * Opção **[1] Criar oportunidade**.
   * Quando perguntado "Salvar como rascunho?", responda `n` para publicar direto.
   * Preencha (ex.: Título "Curso de Java", Modalidade `CURSO`, CH `40`, Vagas `5`).
   * Quando perguntado se é UCE, responda `n` (ou `s` e vincule à `EXT0001` do PPC CC/2020).
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
   * Opção **[13] Painel de progresso de horas** — a barra agora reflete as horas computadas, com a quantidade exigida vinda do PPC vinculado ao aluno.

### Outros fluxos para explorar

* **Versionar PPC** (Coordenador, opção **[7] Gerenciar Cursos (PPC)** → cadastrar nova versão).
* **Solicitar criação de grupo** (Discente, opção **[10]**) e aprovar como Coordenador/Admin.
* **Rascunho de oportunidade** — salve como rascunho, edite (opção dedicada nos menus) e submeta depois.
* **Delegar para a Comissão** — Coordenador delega uma solicitação e a Comissão (`comissao1@ufma.br`) avalia.
* **Desativar usuário** (Admin, opção **[3]**) — depois disso ele não consegue mais logar nem ser vinculado a novos grupos/projetos.

---

## Limitações atuais

* Os dados são reiniciados a cada execução (sem persistência em disco)
* A interface é totalmente baseada em terminal
* Datas livres em texto: o período de realização da oportunidade é uma string, não um `LocalDate` — checagens de prazo de inscrição baseadas em data ainda não são feitas
* Não há sistema de notificações ou alertas automáticos por tempo
