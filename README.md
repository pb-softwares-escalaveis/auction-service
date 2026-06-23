# Auction Service

O Auction Service é o microsserviço central de um ecossistema de leilões, desenvolvido para ser altamente escalável e assíncrono. Ele é o principal responsável pelo gerenciamento dos lotes de leilão e dos lances submetidos pelos usuários, integrado a uma arquitetura orientada a eventos.

## Funcionalidades e Domínio

A aplicação gerencia duas entidades de negócio fundamentais:
*   **Auction Lot (Lote de Leilão)**: Responsável pelo ciclo de vida dos itens anunciados, desde a criação pendente de revisão até a expiração ou venda.
*   **Bid (Lance)**: Registra os lances realizados em um lote, aplicando regras de negócio restritas, como validação de valor mínimo (incremento mínimo de 5%) e verificação de elegibilidade do comprador.

## Tecnologias e Contexto de Uso

Cada tecnologia na stack foi escolhida com um propósito específico para garantir confiabilidade e escalabilidade:

*   **Java 21 e Spring Boot**: Formam a base robusta e moderna da aplicação, provendo a estrutura para os endpoints REST, injeção de dependência e integrações rápidas com as outras bibliotecas através do ecossistema Spring (Data JPA, Validation, WebMVC).
*   **PostgreSQL**: O banco de dados relacional escolhido para garantir a integridade transacional e a forte consistência dos dados cruciais (leilões e lances).
*   **Apache Kafka**: Atua como o barramento principal de eventos do ecossistema. Permite que o Auction Service atue de forma assíncrona, escutando eventos de outros serviços (como criação de usuários, validações de transações financeiras e métricas de avaliação) sem criar acoplamento direto.
*   **Kafka Connect e Debezium**: Utilizados para implementar o padrão Change Data Capture (CDC). Em vez de a aplicação publicar mensagens no Kafka após cada atualização no banco, o Debezium monitora diretamente o log de transações do PostgreSQL (WAL) e publica as mudanças de lotes e lances no Kafka automaticamente. Isso evita dupla escrita e garante consistência.
*   **Netflix Eureka**: Atua como Service Discovery, permitindo que a API encontre dinamicamente e se comunique com outros microsserviços da arquitetura na mesma rede sem depender de IPs fixos.
*   **AWS S3**: Serviço de armazenamento em nuvem escolhido para salvar as mídias e imagens anexadas aos lotes de leilão, removendo a sobrecarga do banco de dados relacional.
*   **Stack de Observabilidade (Actuator, Micrometer/Prometheus, OpenTelemetry, Logstash)**: Conjunto responsável por extrair métricas da JVM, expor health checks, rastrear requisições distribuídas e enviar logs de forma estruturada para serem consumidos por ferramentas de monitoramento.

## Estrutura do Banco de Dados

O banco de dados relacional PostgreSQL é composto por estruturas otimizadas para o domínio:

*   **Tabela `auction_lots`**: Armazena todas as informações do leilão, incluindo título, descrição, identificadores de vendedor e do maior comprador, valores (inicial, atual e de arremate/buy now), data de expiração, status atual e URL da imagem principal (S3).
    *   **Restrições e Integridade (Constraints)**: O banco garante regras em nível de esquema, impedindo valores negativos e assegurando que o preço de arremate seja estritamente superior ao lance inicial (`buy_now_price > initial_bid_price`).
    *   **Indexação**: Possui um índice focado nas colunas de `status` e `expiration_date` (`idx_auction_status_expiration`) para facilitar as buscas contínuas de leilões ativos ou processos agendados que buscam leilões recém-expirados.

*   **Tabela `bids`**: Relaciona os lances aos seus respectivos lotes de leilão e usuários. Guarda o valor exato do lance, a data de criação e o status de validade do lance.
    *   **Indexação Estratégica**: Possui um índice ordenado decrescente de valor (`idx_highest_valid_bid` utilizando `auction_lot_id`, `status` e `amount DESC`), desenhado especificamente para retornar o maior lance válido de um leilão instantaneamente.

## Como Executar Localmente

1. Certifique-se de ter o Docker e Docker Compose instalados.
2. Crie a rede do ecossistema:
   ```bash
   docker network create leilao-network
   ```
3. Duplique o arquivo `.env.example` renomeando-o para `.env` e preencha as credenciais.
4. Suba a aplicação utilizando o docker-compose para inicializar o serviço e suas dependências automaticamente:
   ```bash
   docker-compose up -d
   ```
