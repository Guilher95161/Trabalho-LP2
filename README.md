# Sistema de Extensão UFMA

Projeto em Java desenvolvido para a disciplina de LP2. A aplicação simula, via terminal, um sistema de extensão universitária com diferentes perfis de acesso e operações de gerenciamento acadêmico.

## Objetivo

O sistema tem como objetivo centralizar o gerenciamento de:

* usuários com diferentes papéis
* oportunidades de extensão
* solicitações de aproveitamento de horas
* grupos estudantis
* certificados emitidos ao encerrar oportunidades

## Perfis de usuário

### Discente

* Visualizar oportunidades cadastradas
* Inscrever-se e cancelar inscrições
* Solicitar aproveitamento de horas
* Reenviar solicitações indeferidas
* Consultar certificados e horas cumpridas

### Docente

* Criar oportunidades
* Visualizar oportunidades e grupos
* Encerrar oportunidades
* Gerenciar membros de grupos estudantis

### Gestor

* Avaliar solicitações de aproveitamento
* Criar grupos estudantis
* Visualizar grupos e seus membros

### Administrador

* Acesso completo aos fluxos de usuários, oportunidades, solicitações e grupos

## Tecnologias e características

* Java
* Aplicação de console
* Armazenamento em memória
* Sem uso de banco de dados ou persistência em arquivos

## Requisitos

* O projeto foi pensado no JDK 21, mas é possível usar do JDK 8 ou superior.
* Terminal com suporte aos comandos `javac` e `java`

## Usuários iniciais para teste

O sistema já inicia com alguns usuários cadastrados:

| Papel         | Email            | Senha      |
| ------------- | ---------------- | ---------- |
| Administrador | `admin@ufma.br`  | `admin123` |
| Discente      | `aluno1@ufma.br` | `aluno123` |
| Discente      | `aluno2@ufma.br` | `aluno123` |
| Gestor        | `coord1@ufma.br` | `coord123` |
| Docente       | `doc@ufma.br`    | `doc123`   |

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
javac -d out src\Main.java src\entidades\*.java src\servicos\*.java src\repositorio\*.java src\interfaceterminal\*.java
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
|-- interfaceterminal/
|-- repositorio/
`-- servicos/
```

* `src/Main.java`: ponto de entrada da aplicação
* `src/entidades`: classes de domínio do sistema
* `src/interfaceterminal`: menus e interação com o usuário
* `src/repositorio`: armazenamento central em memória
* `src/servicos`: regras de negócio e orquestração

## Fluxo geral da aplicação

1. O usuário escolhe entre login, cadastro ou encerramento do sistema.
2. Após o login, o menu exibido varia conforme o papel do usuário.
3. As operações são encaminhadas pela interface de terminal para os serviços do sistema.
4. Todos os dados são mantidos apenas em memória durante a execução.

## Limitações atuais

* Os dados são reiniciados a cada execução
* Não há persistência em banco de dados ou arquivos
* A interface é totalmente baseada em terminal
