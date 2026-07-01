# Sistema de Extensão UFMA

Projeto da disciplina de LP2. É uma aplicação Spring Boot que modela um sistema de extensão
universitária: usuários com papéis, oportunidades de extensão, certificados e aproveitamento de
horas.

> Etapa 3 (Spring Boot) concluída. O projeto foi reconstruído do zero no estilo da aula (entidades
> anêmicas com Lombok, JPA, H2). As quatro camadas REST estão completas (`model`, `repo`, `service`
> e `controller`), com as regras de negócio da P2 portadas, persistência em H2 em arquivo com
> seeding, autenticação por Spring Security + JWT, autorização por papel (`@PreAuthorize`/`hasRole`),
> DTOs de resposta (para não expor as entidades cruas) e 112 testes cobrindo services e controllers.

## Objetivo

Centralizar o gerenciamento de:

* usuários com diferentes papéis (docente, coordenador, comissão, administrador)
* oportunidades de extensão (com fila de espera e inscritos)
* certificados emitidos aos discentes
* solicitações de aproveitamento de horas (com parecer, delegação e prazos)

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

Em uma frase: um `Discente` (vinculado a um `Curso`) inscreve-se em `Oportunidades`, recebe
`Certificados`, e abre uma `SolicitacaoAproveitamento` para essas horas serem contadas; quem avalia
são `Usuarios` com o `Papel` adequado.

## Perfis de usuário (via `Papel`)

* Discente: inscreve-se em oportunidades, recebe certificados, solicita aproveitamento de horas.
* Docente: cria/gerencia oportunidades, avalia inscrições, certifica participantes.
* Coordenador: avalia solicitações de aproveitamento (defere/indefere), delega para a comissão.
* Comissão: avalia solicitações delegadas pelo coordenador.
* Administrador: acesso completo; gerencia usuários.

> As regras acima são o escopo de negócio. A camada `service` entrega CRUD, validação e login, além
> das regras mais elaboradas (defere/indefere com prazos 10/5, delegação, certificação, máquinas de
> estado). A autenticação usa Spring Security + JWT (o login emite o token; todo endpoint fora de
> cadastro, login e H2 exige `Authorization: Bearer`). A autorização por papel está ativa: cada
> endpoint exige o `Papel` correto via `@PreAuthorize`/`hasRole`.

## Tecnologias

* Spring Boot 4.0.6 sobre Java 21, build com Maven (wrapper `mvnw`).
* Spring Data JPA + H2 em arquivo (`./data/sistema`, persiste entre execuções; console em `/h2-console`).
* Spring Security + JWT (`jjwt` 0.12.x), autenticação stateless; senha com BCrypt.
* Lombok para o boilerplate das entidades.
* Pacote base `br.ufma.extensao`; camadas flat `model/` (+ `model/dto/`), `repo/`, `service/`
  (+ `service/exceptions/`), `controller/`, `config/` (security).

## Requisitos

* JDK 21 (aponte o `JAVA_HOME` para um JDK 21 ao buildar).
* Não precisa instalar o Maven: o wrapper (`mvnw`) baixa tudo (precisa de internet na 1ª execução).

## Como executar

### PowerShell (Windows)

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"   # ajuste para o seu JDK 21
.\mvnw.cmd spring-boot:run
# app em http://localhost:8080, console H2 em http://localhost:8080/h2-console
```

### Linux/macOS

```bash
export JAVA_HOME=/caminho/para/jdk-21
./mvnw spring-boot:run
```

### IntelliJ IDEA

Abra a pasta (ele reconhece o `pom.xml` como projeto Maven), defina o Project SDK = 21 e rode
`ExtensaoApplication`.

Console H2: JDBC URL `jdbc:h2:file:./data/sistema`, usuário `db`, senha `senha`. As tabelas são
geradas pelo Hibernate a partir do mapeamento (`ddl-auto=update`) e os dados de demonstração são
semeados na primeira execução (ver `DemoDataInitializer`).

Autenticação (JWT): crie um usuário em `POST /api/usuarios` (liberado) e faça login em
`POST /api/usuarios/autenticar` com `{ "email": ..., "senha": ... }`. A resposta traz `{"token":"..."}`
(o mesmo token também vem no header `Authorization`). Envie esse token como `Authorization: Bearer <token>`
nas demais chamadas; todo endpoint fora de cadastro, login e `/h2-console` exige autenticação.

## Testar com Insomnia (tutorial completo)

O repositório já vem com uma coleção pronta do Insomnia: [`insomnia_extensao_ufma.json`](insomnia_extensao_ufma.json).
Ela cobre os 9 controllers (cerca de 65 requisições) em 10 pastas numeradas, com os logins de cada
papel, os corpos JSON já preenchidos e o header `Authorization: Bearer` onde é preciso. O passo a
passo abaixo mostra como testar tudo e verificar a execução ponta a ponta.

### 1. Pré-requisitos

1. Suba a aplicação (`.\mvnw.cmd spring-boot:run`). Ela roda em `http://localhost:8080` e semeia os
   dados de demonstração na primeira execução.
2. Tenha o Insomnia instalado (versão desktop).

### 2. Importar a coleção

No Insomnia: Create / Import, depois Import, From File, e escolha o arquivo
`insomnia_extensao_ufma.json` na raiz do projeto. Isso cria o workspace "Sistema de Extensao UFMA (P3)"
com as pastas:

| # | Pasta | Endpoints |
|---|---|---|
| 1 | Autenticacao | logins de cada papel (ADMINISTRADOR, COORDENADOR, COMISSAO, DOCENTE, discente) |
| 2 | Usuarios | `/api/usuarios` (cadastrar, atualizar, desativar, reativar, remover, buscar) |
| 3 | Discentes | `/api/discentes` (+ painel de horas) |
| 4 | Cursos | `/api/cursos` |
| 5 | Papeis | `/api/papeis` |
| 6 | Certificados | `/api/certificados` |
| 7 | Oportunidades | `/api/oportunidades` (máquina de estados + inscrições) |
| 8 | Solicitacoes de aproveitamento | `/api/solicitacoes` (deferir/indeferir, delegar, reenviar) |
| 9 | Grupos estudantis | `/api/grupos` (membros, cargos, histórico, líder) |
| 10 | Solicitacoes de grupo | `/api/solicitacoes-grupo` (aprovar materializa o grupo) |

### 3. Configurar o Environment e pegar o token

A coleção já traz um Base Environment com duas variáveis:

* `base_url` = `http://localhost:8080` (não precisa mexer)
* `token` = `""` (você preenche depois do login)

Todas as requisições usam `{{ _.base_url }}` na URL e `Authorization: Bearer {{ _.token }}` no header.
Para obter o token:

1. Abra a pasta "1. Autenticacao" e rode, por exemplo, `Login ADMINISTRADOR` (`POST /api/usuarios/autenticar`).
2. A resposta é `{ "token": "eyJ..." }` (o mesmo token vem no header `Authorization`).
3. Copie o valor do `token`, vá no menu de Environments (canto superior esquerdo), depois Manage
   Environments, cole na variável `token` do Base Environment e salve.

Pronto: a partir daqui, toda requisição protegida já envia o `Bearer`. Para testar como outro papel,
basta rodar outro login (por exemplo, `Login COORDENADOR`) e trocar o `token`.

> Dica para automatizar o token: em vez de copiar à mão, você pode deixar a variável `token` como um
> template tag do tipo Response > Body Attribute, apontando para a requisição `Login ...` e o filtro
> JSONPath `$.token`. Aí o token se atualiza sozinho a cada login.

### 4. Usuários já cadastrados (seed)

O `DemoDataInitializer` semeia estes usuários (todos com senha `123`):

| Email | Papel | Usa para |
|---|---|---|
| `admin@ufma.br` | ADMINISTRADOR | gerenciar usuários, cursos e papéis |
| `coord1@ufma.br` | COORDENADOR | avaliar/delegar solicitações de aproveitamento |
| `comissao@ufma.br` | COMISSAO | avaliar solicitações delegadas |
| `doc@ufma.br` | DOCENTE | criar/gerir oportunidades, certificar, grupos |
| `aluno1@ufma.br` | Discente | inscrever-se, receber certificado, pedir aproveitamento |
| `aluno2@ufma.br` | Discente | segundo aluno para testes de fila/substituição |

Além deles, os 7 cenários da P2 já deixam oportunidades, certificados e solicitações em vários
estados (ABERTA, EM_EXECUCAO, PENDENTE, DELEGADA, INDEFERIDA), prontos para inspeção.

### 5. Roteiro ponta a ponta (fluxo completo de uma oportunidade)

Este roteiro exercita a máquina de estados, as inscrições, a certificação e o aproveitamento de
horas do começo ao fim. Os `id`s nas URLs vêm com `1` de exemplo; rode primeiro os `GET .../obter`
para descobrir os ids reais do seu banco e ajuste as URLs.

1. Login como docente: pasta 1, `Login DOCENTE`, copie o token para o Environment.
2. Criar a oportunidade: pasta 7, `Cadastrar oportunidade` (`responsavelId` = id de um docente).
   O corpo já envia `"status": "ABERTA"` para a oportunidade aceitar inscrições de imediato. Se você
   omitir o status, a regra de negócio decide: responsável docente/coordenador abre em `ABERTA`;
   responsável discente nasce `AGUARDANDO_APROVACAO`. Para exercitar a máquina inteira, mande
   `"status": "RASCUNHO"` e passe por `Submeter` e `Aprovar`. Anote o `id` retornado.
3. Inscrever um discente: `Inscrever discente?discenteId=<id do aluno>` (entra na fila de espera).
4. Aprovar a inscrição: `Avaliar inscricao?discenteId=<id>&aprovar=true` (sai da fila, ocupa vaga).
5. Iniciar: `Iniciar` (`ABERTA -> EM_EXECUCAO`).
6. Encerrar: `Encerrar` (`EM_EXECUCAO -> ENCERRADA`).
7. Certificar: `Certificar participantes` com corpo `[<id do aluno>]` (gera o `Certificado`).
8. Login como coordenador: pasta 1, `Login COORDENADOR`, troque o token.
9. Abrir a solicitação de aproveitamento: pasta 8, `Criar solicitacao`
   (`solicitanteId` = aluno, `certificadoId` = id do certificado gerado). Nasce `PENDENTE`.
10. Deferir: `Avaliar (deferir/indeferir)?aprovado=true&parecer=...`. Ao deferir, as horas do
    certificado entram no total do discente.
11. Conferir o resultado: pasta 3, `Painel de horas` (`/api/discentes/<id>/painel-horas`). Veja
    `horasCumpridas`, `metaHoras`, `horasRestantes` e `percentualConcluido` atualizados.

> Variações para explorar: `Cancelar` a oportunidade grava o `motivoCancelamento`; `Delegar para
> comissao` move a solicitação para a fila da comissão; `Reenviar` reabre uma solicitação `INDEFERIDA`
> dentro do prazo de 5 dias. Na pasta 9, `Adicionar membro`, `Definir cargo do membro?cargo=PRESIDENTE`
> e `Discente e lider?` demonstram cargos/histórico; na pasta 10, `Avaliar (aprovar cria o grupo)`
> materializa um `GrupoEstudantil` a partir da solicitação.

### 6. Verificar a autorização por papel (403)

Para confirmar que a segurança por papel está ativa:

1. Rode `Login discente (aluno1)` (pasta 1) e coloque esse token no Environment.
2. Tente `Cadastrar papel` (pasta 5, exige ADMINISTRADOR) ou `Cadastrar curso` (pasta 4).
3. A resposta deve ser 403 Forbidden, porque o discente não tem o papel exigido.
4. Troque para `Login ADMINISTRADOR`, refaça a mesma chamada e ela passa (200/201).

Sem token (ou com token inválido), os endpoints protegidos respondem 401/403; só cadastro
(`POST /api/usuarios`, `POST /api/discentes`), login e `/h2-console/**` ficam liberados.

### 7. Dicas

* Os `id`s nas URLs são exemplos (`1`). Rode um `GET .../obter` (ou `Buscar por id`) antes para pegar
  os ids reais do seu banco.
* Como o H2 é em arquivo (`./data/sistema`), os dados persistem entre execuções; o seed só roda com
  o banco vazio. Para recomeçar do zero, pare a aplicação e apague a pasta `data/`.
* Dá para inspecionar o banco direto no console H2 (`http://localhost:8080/h2-console`, JDBC
  `jdbc:h2:file:./data/sistema`, usuário `db`, senha `senha`).

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
pom.xml, mvnw, .mvn/   (build Maven)
```

## Estado atual

Todas as fases da etapa 3 estão concluídas:

* Scaffold + model + repo: Spring Boot subindo em `:8080`; 11 entidades JPA, 9 `JpaRepository`.
* Services: 9 `@Service`/`@Transactional` com CRUD, validação, login e as regras mais elaboradas:
  * Oportunidade: máquina de estados (RF012) + inscrições (fila/aprovados/substituir/certificar) + `motivoCancelamento`.
  * Aproveitamento: máquina de estados `StatusSolicitacao`, deferimento que soma horas ao discente, delegação, cancelamento e reenvio com prazos 10/5.
  * Grupos: aprovação que cria o grupo (o solicitante vira `PRESIDENTE`), gerência de membros/cargos com histórico, `isLider`.
  * Painel de horas do discente e desativar/reativar conta (RF0001/RF004).
* Controllers: 9 `@RestController` com endpoints `/api/*`; DTOs de resposta (`*Response`) em todos os
  endpoints que retornam entidades; `@PreAuthorize`/`hasRole` por ação.
* Spring Security + JWT: login em `POST /api/usuarios/autenticar` retorna token; BCrypt na senha;
  `SecurityFilterChain` STATELESS.
* Persistência em H2 em arquivo + seeding dos 7 cenários (`DemoDataInitializer`); testes em H2 em
  memória isolado.
* 112 testes passando: services (52) e controllers (55) + contextLoads; cobertura de CRUD, regras de
  negócio, autorização (403 sem o papel correto) e cenários de erro.
* FIFO nas inscrições: `filaEspera` e `inscritosAprovados` usam `List<Discente>` com `@OrderColumn`,
  garantindo a ordem de inserção no reload.

> O versionamento de PPC/UCE continua colapsado em `Curso` (decisão da disciplina). A feature de
> grupos estudantis foi reintroduzida. O histórico da etapa 2 permanece nas branches `P2` e no git.
