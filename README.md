# Sistema de Extensao UFMA (LP2)

Projeto em Java desenvolvido para a disciplina de LP2, simulando um sistema de extensao universitaria em terminal.

O sistema permite o gerenciamento de:
- usuarios com papeis diferentes (discente, docente, gestor e administrador)
- oportunidades de extensao
- solicitacoes de aproveitamento de horas
- grupos estudantis
- emissao de certificados ao encerrar oportunidades

## Tecnologias

- Java (aplicacao de console)
- armazenamento em memoria (sem banco de dados)

## Funcionalidades principais

### Discente
- visualizar oportunidades
- inscrever/cancelar inscricao em oportunidades
- solicitar aproveitamento de horas
- reenviar solicitacoes indeferidas
- visualizar certificados e horas cumpridas

### Docente
- criar e encerrar oportunidades
- visualizar grupos
- gerenciar membros de grupos

### Gestor
- avaliar solicitacoes de aproveitamento
- criar e visualizar grupos estudantis

### Administrador
- acesso completo a funcionalidades de usuarios, oportunidades, solicitacoes e grupos

## Usuarios iniciais para teste

Ja existem usuarios pre-cadastrados no sistema:

- Administrador: `admin@ufma.br` / `admin123`
- Discente: `aluno1@ufma.br` / `aluno123`
- Discente: `aluno2@ufma.br` / `aluno123`
- Gestor: `coord1@ufma.br` / `coord123`
- Docente: `doc@ufma.br` / `doc123`

## Como rodar o projeto

### Opcao 1: IntelliJ IDEA (recomendado)
1. Abra a pasta do projeto no IntelliJ.
2. Aguarde o indice e configuracoes de SDK.
3. Execute a classe `Main` (`src/Main.java`).

### Opcao 2: Terminal no Windows (PowerShell)
No diretorio raiz do projeto, execute:

```powershell
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object {$_.FullName})
java -cp out Main
```

### Opcao 3: Terminal no Windows (CMD)
No diretorio raiz do projeto, execute:

```bat
if not exist out mkdir out
javac -d out src\Main.java src\entidades\*.java src\servicos\*.java src\repositorio\*.java src\interfaceterminal\*.java
java -cp out Main
```

### Opcao 4: Terminal no Linux/macOS (bash/zsh)
No diretorio raiz do projeto, execute:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out Main
```

## Estrutura do projeto

- `src/entidades`: classes de dominio (Usuario, Oportunidade, GrupoEstudantil, etc.)
- `src/servicos`: regras de negocio
- `src/repositorio`: repositorio em memoria
- `src/interfaceterminal`: menus e interacao via console
- `src/Main.java`: ponto de entrada da aplicacao

## Observacoes

- Os dados existem apenas durante a execucao (nao ha persistencia em arquivo ou banco).
- Ao encerrar o programa, os dados voltam ao estado inicial na proxima execucao.
