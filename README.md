# Sistema de Extensão UFMA

Projeto da disciplina de LP2. Aplicação **Spring Boot** que modela um sistema de extensão
universitária: usuários com papéis, oportunidades de extensão, certificados e aproveitamento de
horas.

> **Etapa 3 (Spring Boot) — concluída.** O projeto foi **reconstruído do zero** no estilo da
> aula (entidades anêmicas com Lombok, JPA, H2). As quatro camadas REST estão completas (**`model`**,
> **`repo`**, **`service`**, **`controller`**) com as **regras de negócio ricas** da P2 portadas,
> **persistência em H2 em arquivo + seeding**, **autenticação Spring Security + JWT**,
> **autorização por papel** (`@PreAuthorize`/`hasRole`), **DTOs de resposta** (sem expor entidades
> cruas) e **112 testes** cobrindo services e controllers.

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

> As regras acima são o **escopo de negócio**. A camada `service` entrega **CRUD + validação +
> login** e as **regras ricas** (defere/indefere com prazos 10/5, delegação, certificação, máquinas
> de estado). A **autenticação** é feita por **Spring Security + JWT** (login emite token; todo
> endpoint fora de cadastro/login/H2 exige `Authorization: Bearer`). A **autorização por papel**
> está ativa: cada endpoint exige o `Papel` correto via `@PreAuthorize`/`hasRole`.

## Tecnologias

* **Spring Boot 4.0.6** sobre **Java 21**, build com **Maven** (wrapper `mvnw`).
* **Spring Data JPA** + **H2 em arquivo** (`./data/sistema`, persiste entre execuções; console em `/h2-console`).
* **Spring Security + JWT** (`jjwt` 0.12.x) — autenticação stateless; senha com **BCrypt**.
* **Lombok** para o boilerplate das entidades.
* Pacote base `br.ufma.extensao`; camadas flat `model/` (+ `model/dto/`), `repo/`, `service/`
  (+ `service/exceptions/`), `controller/`, `config/` (security).

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

**Console H2:** JDBC URL `jdbc:h2:file:./data/sistema`, usuário `db`, senha `senha`. As tabelas são
geradas pelo Hibernate a partir do mapeamento (`ddl-auto=update`) e os dados de demonstração são
semeados na primeira execução (ver `DemoDataInitializer`).

**Autenticação (JWT):** crie um usuário em `POST /api/usuarios` (liberado) e faça login em
`POST /api/usuarios/autenticar` com `{ "email": ..., "senha": ... }`. A resposta traz `{"token":"..."}`
(também no header `Authorization`). Envie esse token como `Authorization: Bearer <token>` nas demais
chamadas — todo endpoint fora de cadastro/login/`/h2-console` exige autenticação. O roteiro de testes
ponta a ponta está em `ROTEIRO_TESTES.md`.

## Estrutura do projeto

```text
src/main/java/br/ufma/extensao/
|-- ExtensaoApplication.java   (entry @SpringBootApplication + bean BCryptPasswordEncoder)
|-- model/                     (11 entidades anêmicas + enums/ + dto/)
|   |-- enums/                 (StatusOportunidade, StatusSolicitacao, ModalidadeOportunidade, CargoGrupo)
|   `-- dto/                   (DTOs de requisição + DTOs de resposta *Response)
|-- repo/                      (9 interfaces JpaRepository)
|-- service/                   (9 services @Service/@Transactional + exceptions/)
|   `-- exceptions/            (SistemaExtensaoException + RegraNegocioRunTime + 4 especializadas)
|-- controller/                (9 @RestController, endpoints /api/*, @PreAuthorize por ação)
`-- config/                    (SecurityConfig + JwtAuthorizationFilter + JwtService + SecurityConstants + DemoDataInitializer)
src/main/resources/application.properties        (H2 em arquivo + console)
src/test/resources/application.properties        (H2 em memória, isolado, create-drop)
pom.xml · mvnw · .mvn/   (build Maven)
```

## Estado atual

Todas as fases da etapa 3 estão concluídas:

* ✅ **Scaffold + model + repo** — Spring Boot subindo em `:8080`; 11 entidades JPA, 9 `JpaRepository`.
* ✅ **Services** — 9 `@Service`/`@Transactional` com CRUD + validação + login + regras ricas:
  * **Oportunidade** — máquina de estados (RF012) + inscrições (fila/aprovados/substituir/certificar) + `motivoCancelamento`.
  * **Aproveitamento** — máquina de estados `StatusSolicitacao`, **deferimento que soma horas ao discente**, delegação, cancelamento e reenvio com **prazos 10/5**.
  * **Grupos** — aprovação que **cria o grupo** (solicitante vira `PRESIDENTE`), gerência de membros/cargos com histórico, `isLider`.
  * **Painel de horas** do discente e **desativar/reativar conta** (RF0001/RF004).
* ✅ **Controllers** — 9 `@RestController` com endpoints `/api/*`; **DTOs de resposta** (`*Response`) em todos os endpoints que retornam entidades; **`@PreAuthorize`/`hasRole`** por ação.
* ✅ **Spring Security + JWT** — login em `POST /api/usuarios/autenticar` retorna token; BCrypt na senha; `SecurityFilterChain` STATELESS.
* ✅ **Persistência em H2 em arquivo** + **seeding** dos 7 cenários (`DemoDataInitializer`); testes em H2 em memória isolado.
* ✅ **112 testes** passando — services (52) e controllers (55) + contextLoads; cobertura de CRUD, regras de negócio, autorização (403 sem papel correto) e cenários de erro.
* ✅ **FIFO nas inscrições** — `filaEspera` e `inscritosAprovados` usam `List<Discente>` com `@OrderColumn`, garantindo ordem de inserção no reload.

> O **versionamento de PPC/UCE** continua colapsado em `Curso` (decisão da disciplina). A feature de
> **grupos estudantis** foi **reintroduzida**. O histórico da etapa 2 permanece nas branches `P2` e
> no git.
