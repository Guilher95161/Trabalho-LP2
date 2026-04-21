# Sistema de Extensão UFMA (LP2)

Projeto em Java desenvolvido para a disciplina de LP2, simulando um sistema de extensao universitaria em terminal.

O sistema permite o gerenciamento de:
- usuários com papéis diferentes (discente, docente, gestor e administrador)
- oportunidades de extensão
- solicitações de aproveitamento de horas
- grupos estudantis
- emissão de certificados ao encerrar oportunidades

## Tecnologias

- Java (aplicação de console)
- armazenamento em memória (sem banco de dados)

## Funcionalidades principais

### Discente
- visualizar oportunidades
- inscrever/cancelar inscrição em oportunidades
- solicitar aproveitamento de horas
- reenviar solicitações indeferidas
- visualizar certificados e horas cumpridas

### Docente
- criar e encerrar oportunidades
- visualizar grupos
- gerenciar membros de grupos

### Gestor
- avaliar solicitações de aproveitamento
- criar e visualizar grupos estudantis

### Administrador
- acesso completo a funcionalidades de usuários, oportunidades, solicitações e grupos

## Usuários iniciais para teste

Já existem usuários pre-cadastrados no sistema:

- Administrador: `admin@ufma.br` / `admin123`
- Discente: `aluno1@ufma.br` / `aluno123`
- Discente: `aluno2@ufma.br` / `aluno123`
- Gestor: `coord1@ufma.br` / `coord123`
- Docente: `doc@ufma.br` / `doc123`

## Como rodar o projeto

### Opção 1: IntelliJ IDEA (recomendado)
1. Abra a pasta do projeto no IntelliJ.
2. Aguarde o índice e configurações de SDK.
3. Execute a classe `Main` (`src/Main.java`).

### Opção 2: Terminal no Windows (PowerShell)
No diretório raiz do projeto, execute:

```powershell
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object {$_.FullName})
java -cp out Main
```

### Opção 3: Terminal no Windows (CMD)
No diretório raiz do projeto, execute:

```bat
if not exist out mkdir out
javac -d out src\Main.java src\entidades\*.java src\servicos\*.java src\repositorio\*.java src\interfaceterminal\*.java
java -cp out Main
```

### Opção 4: Terminal no Linux/macOS (bash/zsh)
No diretório raiz do projeto, execute:

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out Main
```

## Estrutura do projeto

- `src/entidades`: classes de domínio (Usuario, Oportunidade, GrupoEstudantil, etc.)
- `src/servicos`: regras de negócio
- `src/repositorio`: repositorio em memória
- `src/interfaceterminal`: menus e interação via console
- `src/Main.java`: ponto de entrada da aplicação

## Observacoes

- Os dados existem apenas durante a execução (não ha persistência em arquivo ou banco).
- Ao encerrar o programa, os dados voltam ao estado inicial na proxima execução.
