# Sistema de Extensão UFMA

Projeto da disciplina de LP2. Aplicação **Spring Boot** que modela um sistema de extensão
universitária: usuários com papéis, oportunidades de extensão, certificados e aproveitamento de
horas.

> **Etapa 3 (Spring Boot) — em andamento.** O projeto foi **reconstruído do zero** no estilo da
> aula (entidades anêmicas com Lombok, JPA, H2). Estão prontas as camadas **`model`**, **`repo`**,
> **`service`** e **`controller`** (REST). Os services/controllers entregam **CRUD + validação +
> login** (estilo dos PDFs da aula); as **regras de negócio ricas** (máquinas de estado, prazos,
> aproveitamento de horas) entram nos próximos passos.

## Objetivo

Centralizar o gerenciamento de:

* usuários com diferentes **papéis** (docente, coordenador, comissão, administrador)
* **oportunidades** de extensão (com fila de espera e inscritos)
* **certificados** emitidos aos discentes
* **solicitações de aproveitamento** de horas (com parecer, delegação e prazos)

## Domínio (11 entidades)

| Entidade | Papel no sistema |
|---|---|
| `Usuario` | pessoa com login; acumula 0+ `Papel` (M:N) |
| `Discente` | é um `Usuario` (subclasse) com dados de aluno: `horasCumpridas`, `certificados`, `curso` |
| `Papel` | papel/permissão (DOCENTE, COORDENADOR, COMISSAO, ADMINISTRADOR) |
| `Curso` | curso + currículo + carga horária de extensão exigida |
| `Oportunidade` | atividade de extensão (fila de espera + inscritos aprovados) |
| `Certificado` | comprovante de horas do aluno |
| `SolicitacaoAproveitamento` | pedido para que um certificado conte como horas |
| `GrupoEstudantil` | grupo/liga sob responsabilidade de um `Usuario` (papel docente); agrega membros e histórico de cargos |
| `MembroGrupo` | vínculo discente + cargo + data de entrada num grupo |
| `HistoricoCargo` | trilha temporal de cargos de um discente no grupo |
| `SolicitacaoGrupoEstudantil` | pedido de um discente para criação de grupo |

Enums: `StatusOportunidade`, `StatusSolicitacao`, `ModalidadeOportunidade`, `CargoGrupo`.

**Em uma frase:** um `Discente` (vinculado a um `Curso`) inscreve-se em `Oportunidades`, recebe
`Certificados`, e abre uma `SolicitacaoAproveitamento` para essas horas serem contadas; quem avalia
são `Usuarios` com o `Papel` adequado.

## Perfis de usuário (via `Papel`)

* **Discente** — inscreve-se em oportunidades, recebe certificados, solicita aproveitamento de horas.
* **Docente** — cria/gerencia oportunidades, avalia inscrições, certifica participantes.
* **Coordenador** — avalia solicitações de aproveitamento (defere/indefere), delega para a comissão.
* **Comissão** — avalia solicitações delegadas pelo coordenador.
* **Administrador** — acesso completo; gerencia usuários.

> As regras acima são o **escopo de negócio**. A camada `service` já entrega o **CRUD + validação +
> login**; as regras ricas (defere/indefere com prazos, delegação, certificação) entram numa próxima
> etapa.

## Tecnologias

* **Spring Boot 4.0.6** sobre **Java 21**, build com **Maven** (wrapper `mvnw`).
* **Spring Data JPA** + **H2 em memória** (console em `/h2-console`).
* **Lombok** para o boilerplate das entidades.
* Pacote base `br.ufma.extensao`; camadas flat `model/` (+ `model/dto/`), `repo/`, `service/`
  (+ `service/exceptions/`), `controller/`.

## Requisitos

* **JDK 21** (aponte o `JAVA_HOME` para um JDK 21 ao buildar).
* Não precisa instalar Maven — o **Maven Wrapper** (`mvnw`) baixa tudo (internet na 1ª execução).

## Como executar

### PowerShell (Windows)

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"   # ajuste para o seu JDK 21
.\mvnw.cmd spring-boot:run
# app em http://localhost:8080 · console H2 em http://localhost:8080/h2-console
```

### Linux/macOS

```bash
export JAVA_HOME=/caminho/para/jdk-21
./mvnw spring-boot:run
```

### IntelliJ IDEA

Abra a pasta (reconhece o `pom.xml` como projeto **Maven**), Project SDK = **21**, rode
`ExtensaoApplication`.

**Console H2:** JDBC URL `jdbc:h2:mem:sistema`, usuário `db`, senha `senha`. As tabelas são geradas
pelo Hibernate a partir do mapeamento (`ddl-auto=update`).

## Estrutura do projeto

```text
src/main/java/br/ufma/extensao/
|-- ExtensaoApplication.java   (entry @SpringBootApplication)
|-- model/                     (11 entidades anêmicas + enums/ + dto/)
|   |-- enums/                 (StatusOportunidade, StatusSolicitacao, ModalidadeOportunidade, CargoGrupo)
|   `-- dto/                   (DTOs de requisição dos controllers)
|-- repo/                      (9 interfaces JpaRepository)
|-- service/                   (9 services @Service + exceptions/)
|   `-- exceptions/            (SistemaExtensaoException + RegraNegocioRunTime + 4 especializadas)
`-- controller/                (9 @RestController, endpoints /api/*)
src/main/resources/application.properties   (H2 em memória + console)
pom.xml · mvnw · .mvn/   (build Maven)
```

## Estado atual e próximos passos

* ✅ Scaffold Spring Boot subindo em `:8080`; schema JPA gerado no H2.
* ✅ Camada `model` (11 entidades, herança JOINED em `Discente`, `Papel` M:N, grupos estudantis) e
  `repo` (9 repositórios).
* ✅ Camada `service` (9 services) com **CRUD + validação + login** e hierarquia de exceções de domínio.
* ✅ Camada `controller` (9 `@RestController`) com endpoints REST `/api/*`, DTOs e respostas `ResponseEntity`.
* ✅ Primeiras regras ricas: **máquina de estados da Oportunidade** (`submeter/aprovar/iniciar/encerrar/cancelar`, RF012) e **desativar conta** (RF0001).
* ⬜ Demais regras ricas (prazos 10/5 e deferimento do aproveitamento, painel de horas, cargos/líder, aprovação de grupo).
* ⬜ Validação de papéis nas ações, seeding de dados e Spring Security.

> O **versionamento de PPC/UCE** continua colapsado em `Curso` (decisão da disciplina). A feature de
> **grupos estudantis** foi **reintroduzida**. O histórico completo da etapa 2 permanece nas branches
> `P2` e no histórico do git.
