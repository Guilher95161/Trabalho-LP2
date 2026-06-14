# Sistema de Extensão UFMA

Projeto da disciplina de LP2. Aplicação **Spring Boot** que modela um sistema de extensão
universitária: usuários com papéis, oportunidades de extensão, certificados e aproveitamento de
horas.

> **Etapa 3 (Spring Boot) — em andamento.** O projeto foi **reconstruído do zero** no estilo da
> aula (entidades anêmicas com Lombok, JPA, H2). Estão prontas as camadas **`model`** e **`repo`**;
> a camada **`service`** (regras de negócio) e o **REST** entram nos próximos passos.

## Objetivo

Centralizar o gerenciamento de:

* usuários com diferentes **papéis** (docente, coordenador, comissão, administrador)
* **oportunidades** de extensão (com fila de espera e inscritos)
* **certificados** emitidos aos discentes
* **solicitações de aproveitamento** de horas (com parecer, delegação e prazos)

## Domínio (7 entidades)

| Entidade | Papel no sistema |
|---|---|
| `Usuario` | pessoa com login; acumula 0+ `Papel` (M:N) |
| `Discente` | é um `Usuario` (subclasse) com dados de aluno: `horasCumpridas`, `certificados`, `curso` |
| `Papel` | papel/permissão (DOCENTE, COORDENADOR, COMISSAO, ADMINISTRADOR) |
| `Curso` | curso + currículo + carga horária de extensão exigida |
| `Oportunidade` | atividade de extensão (fila de espera + inscritos aprovados) |
| `Certificado` | comprovante de horas do aluno |
| `SolicitacaoAproveitamento` | pedido para que um certificado conte como horas |

Enums: `StatusOportunidade`, `StatusSolicitacao`, `ModalidadeOportunidade`.

**Em uma frase:** um `Discente` (vinculado a um `Curso`) inscreve-se em `Oportunidades`, recebe
`Certificados`, e abre uma `SolicitacaoAproveitamento` para essas horas serem contadas; quem avalia
são `Usuarios` com o `Papel` adequado.

## Perfis de usuário (via `Papel`)

* **Discente** — inscreve-se em oportunidades, recebe certificados, solicita aproveitamento de horas.
* **Docente** — cria/gerencia oportunidades, avalia inscrições, certifica participantes.
* **Coordenador** — avalia solicitações de aproveitamento (defere/indefere), delega para a comissão.
* **Comissão** — avalia solicitações delegadas pelo coordenador.
* **Administrador** — acesso completo; gerencia usuários.

> As regras acima são o **escopo de negócio**; sua implementação vive na camada `service`, ainda a
> ser escrita. Hoje o projeto entrega o modelo de dados (entidades + repositórios) que as sustenta.

## Tecnologias

* **Spring Boot 4.0.6** sobre **Java 21**, build com **Maven** (wrapper `mvnw`).
* **Spring Data JPA** + **H2 em memória** (console em `/h2-console`).
* **Lombok** para o boilerplate das entidades.
* Pacote base `br.ufma.extensao`; camadas flat `model/`, `repo/`, `service/`.

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
|-- model/                     (7 entidades anêmicas + enums/)
|   `-- enums/                 (StatusOportunidade, StatusSolicitacao, ModalidadeOportunidade)
|-- repo/                      (6 interfaces JpaRepository)
`-- service/                   (reservada — próximo passo)
src/main/resources/application.properties   (H2 em memória + console)
pom.xml · mvnw · .mvn/   (build Maven)
```

## Estado atual e próximos passos

* ✅ Scaffold Spring Boot subindo em `:8080`; schema JPA gerado no H2.
* ✅ Camada `model` (7 entidades, herança JOINED em `Discente`, `Papel` M:N) e `repo` (6 repositórios).
* ⬜ Camada `service` com as regras de negócio (máquinas de estado, prazos, aproveitamento de horas).
* ⬜ DTOs + controllers REST, tratamento global de exceções, seeding de dados, Spring Security.

> O escopo foi **reduzido** em relação à etapa 2 (removida a feature de grupos estudantis e o
> versionamento de PPC/UCE) para aproximar do projeto de referência da disciplina. O histórico
> completo da etapa 2 permanece nas branches `P2` e no histórico do git.
