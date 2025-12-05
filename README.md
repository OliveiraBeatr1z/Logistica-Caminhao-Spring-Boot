# 🚚 FrotaLux - Sistema de Gestão de Frota e Logística

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

> Sistema completo de gestão de frota e logística com cálculo de frete baseado em cubagem, otimização de carga e controle preventivo de manutenção.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Instalação](#-instalação)
- [Como Usar](#-como-usar)
- [API REST](#-api-rest)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Documentação](#-documentação)

---

## 🎯 Sobre o Projeto

O **FrotaLux** é um sistema completo de gestão de frota desenvolvido em **Spring Boot** que implementa:

- ✅ **Cálculo de frete inteligente** baseado em cubagem (300 kg/m³)
- ✅ **Sistema de manutenção preventiva** automática (10.000 km e 70.000 km)
- ✅ **Otimização de carga** com algoritmo de agrupamento
- ✅ **Rastreamento em 4 etapas** (Coleta → Processamento → A Caminho → Entregue)
- ✅ **Gestão de motoristas** com validação de CNH
- ✅ **Sistema de pagamento** completo
- ✅ **Avaliações** de cliente e recebedor
- ✅ **API REST** para integração mobile

---

## ⚡ Funcionalidades

### 📦 Gestão de Frota

#### **1. Caminhões**
- Cadastro completo (modelo, placa, marca, ano)
- Dimensões (comprimento, largura, altura)
- Fator de cubagem (300 kg/m³)
- Cálculo automático de volume e peso cubado
- Valor por km rodado

#### **2. Motoristas**
- Cadastro com CPF e CNH
- Validação de CNH (categoria e validade)
- Status ativo/inativo
- Rastreamento GPS em tempo real
- Alertas de CNH vencendo (30 dias)

#### **3. Caixas Padronizadas**
- Dimensões e material
- Limite de peso
- Valor fixo por caixa
- Validação automática de produtos

---

### 🚛 Gestão Logística

#### **4. Solicitações de Transporte**
- Cadastro de produto (dimensões e peso)
- Origem e destino (coordenadas + endereços)
- **Cálculo inteligente de frete**:
  - Peso cubado vs peso real (usa o maior)
  - Cobrança por peso OU por caixa (usa o menor)
  - Taxa por km rodado
  - Pedágio estimado
- **4 etapas de rastreamento**:
  1. 🟡 Coleta
  2. 🔵 Em Processamento
  3. 🟠 A Caminho da Entrega
  4. 🟢 Entregue

#### **5. Manutenção Preventiva** 🔧
- **Sistema de alertas automáticos**:
  - ⚠️ Alerta a cada **10.000 km** (óleo, filtros, pastilhas)
  - ⚠️ Alerta a cada **70.000 km** (troca de pneus)
- **Níveis de criticidade**:
  - 🔴 **CRÍTICO**: Manutenção atrasada
  - 🟡 **AVISO**: Faltam 1.000 km ou 5.000 km
  - ℹ️ **INFORMATIVO**: Tudo em dia
- Histórico completo de manutenções
- Cálculo automático da próxima manutenção

#### **6. Percursos/Viagens**
- Registro de saída e chegada
- Quilometragem inicial e final
- Controle de combustível (litros e custo)
- Cálculos automáticos:
  - Distância percorrida
  - Consumo médio (km/l)
  - Custo por km

---

### 🎯 Otimização e Inteligência

#### **7. Otimização de Carga** 📊
- **Algoritmo inteligente de agrupamento**:
  - Agrupa entregas por região
  - Sugere melhor caminhão (minimiza desperdício)
  - Calcula taxa de ocupação (peso + volume)
  - Estima economia ao agrupar rotas
- **Dashboard visual**:
  - Grupos otimizados com cores
  - Taxa de ocupação por grupo
  - Economia total estimada
  - Solicitações por região

#### **8. Sistema de Pagamento** 💳
- Criação automática ao solicitar transporte
- Status: Pendente → Processando → Confirmado
- Métodos: PIX, Cartão, Boleto
- Webhooks para confirmação
- Cálculo de receita por período

#### **9. Avaliações e Feedback** ⭐
- Cliente avalia a entrega (1-5 estrelas)
- Recebedor avalia a entrega (1-5 estrelas)
- Comentários opcionais
- Média automática
- Estatísticas gerais do serviço

---

## 🛠️ Tecnologias

### Backend
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.5** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM
- **Lombok** - Redução de boilerplate
- **Bean Validation** - Validações

### Frontend
- **Thymeleaf** - Template engine
- **Bootstrap 4** - Framework CSS
- **FontAwesome** - Ícones
- **jQuery** - JavaScript library

### Banco de Dados
- **MySQL 8** - Banco de dados relacional
- **HikariCP** - Pool de conexões

### APIs Externas (Preparadas)
- **Haversine** - Cálculo de distância (implementado)
- **OpenRouteService** - Rotas reais (preparado)
- **Google Maps** - Distância e pedágios (preparado)
- **Twilio** - WhatsApp (preparado para integração)

---

## 🏗️ Arquitetura

O projeto segue os princípios **SOLID** e **Clean Architecture**:

```
┌─────────────────────────────────────────┐
│           Controllers (Web/API)         │
├─────────────────────────────────────────┤
│              Services                   │
│    (Lógica de Negócio)                 │
├─────────────────────────────────────────┤
│            Repositories                 │
│        (Acesso a Dados)                │
├─────────────────────────────────────────┤
│      Entidades (Domain Model)          │
└─────────────────────────────────────────┘
```

### Princípios SOLID Aplicados

- **S** - Single Responsibility: Cada classe tem uma única responsabilidade
- **O** - Open/Closed: Extensível sem modificação (enums, interfaces)
- **L** - Liskov Substitution: Interfaces implementadas corretamente
- **I** - Interface Segregation: DTOs específicos para cada operação
- **D** - Dependency Inversion: Injeção de dependências

---

## 📥 Instalação

### Pré-requisitos

- **Java 21** ou superior
- **Maven 3.8+**
- **MySQL 8.0+**
- **Git**

### Passo a Passo

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/frotalux.git
cd frotalux
```

2. **Configure o banco de dados**

Crie um banco de dados MySQL:
```sql
CREATE DATABASE frota CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Configure o `application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/frota
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

4. **Compile o projeto**
```bash
./mvnw clean install
```

5. **Execute a aplicação**
```bash
./mvnw spring-boot:run
```

6. **Acesse o sistema**
```
http://localhost:8083
```

---

## 🚀 Como Usar

### Interface Web

#### **Dashboard Principal**
```
http://localhost:8083
```

#### **Gestão de Motoristas**
```
http://localhost:8083/motoristas/cadastrar  → Cadastrar novo motorista
http://localhost:8083/motoristas/mostrar    → Listar todos
http://localhost:8083/motoristas/disponiveis → Ver disponíveis
```

#### **Manutenção Preventiva**
```
http://localhost:8083/manutencoes/alertas   → Ver alertas (IMPORTANTE!)
http://localhost:8083/manutencoes/mostrar   → Histórico
http://localhost:8083/manutencoes/cadastrar → Registrar manutenção
```

#### **Otimização de Carga**
```
http://localhost:8083/otimizacao → Dashboard de otimização
```

#### **Solicitações de Transporte**
```
http://localhost:8083/solicitacoes/cadastrar → Nova solicitação
http://localhost:8083/solicitacoes/mostrar   → Listar todas
```

---

## 🔌 API REST

### 📋 Documentação Completa das APIs

O sistema possui **66+ endpoints REST** completamente documentados:

#### 📄 Documentos Disponíveis
- **[ROTAS_API_POSTMAN.md](ROTAS_API_POSTMAN.md)** - Guia completo com todos os endpoints
- **[Frotalux_Postman_Collection.json](Frotalux_Postman_Collection.json)** - Coleção para importar no Postman
- **[RESUMO_ROTAS_REST.md](RESUMO_ROTAS_REST.md)** - Resumo executivo das APIs

#### 🚀 Quick Start - Postman

1. Importe a coleção: `Frotalux_Postman_Collection.json`
2. Configure a variável `baseUrl = http://localhost:8080`
3. Execute os endpoints!

### Base URL
```
http://localhost:8080/api
```

### 📦 Módulos de API

| Módulo | Endpoints | Descrição |
|--------|-----------|-----------|
| **Caminhões** | 5 | CRUD completo de caminhões |
| **Marcas** | 5 | Gestão de marcas de caminhões |
| **Motoristas** | 4 | Cadastro e listagem de motoristas |
| **Caixas** | 3 | Caixas padronizadas |
| **Solicitações** | 4 | Criação e gestão de transportes |
| **Percursos** | 6 | Controle de viagens |
| **Manutenções** | 4 | Sistema de manutenção preventiva |
| **Pagamentos** | 6 | Gestão de pagamentos |
| **Avaliações** | 11 | Sistema duplo de avaliação |
| **Otimização** | 3 | Algoritmo de otimização de carga |
| **Rastreamento** | 5 | GPS e rastreamento em tempo real |
| **Dashboard** | 5 | KPIs e estatísticas |
| **API Mobile** | 5 | Endpoints para app do motorista |

### Endpoints Principais

#### **Cotação de Frete**
```http
POST /api/solicitacoes/cotacao
Content-Type: application/json

{
  "produto": "Notebook",
  "comprimento": 0.4,
  "largura": 0.3,
  "altura": 0.1,
  "pesoReal": 2.5,
  "origemLat": -23.550520,
  "origemLon": -46.633308,
  "destinoLat": -22.903539,
  "destinoLon": -43.209587
}
```

#### **Criar Solicitação**
```http
POST /api/solicitacoes
Content-Type: application/json

{
  "produto": "Notebook",
  "comprimento": 0.4,
  "largura": 0.3,
  "altura": 0.1,
  "pesoReal": 2.5,
  "origemLat": -23.550520,
  "origemLon": -46.633308,
  "destinoLat": -22.903539,
  "destinoLon": -43.209587,
  "nomeCliente": "João Silva",
  "nomeRecebedor": "Maria Santos"
}
```

#### **Atualizar Localização (App Motorista)**
```http
POST /api/motorista/{id}/localizacao
Content-Type: application/json

{
  "latitude": -23.550520,
  "longitude": -46.633308
}
```

#### **Atualizar Status da Entrega**
```http
POST /api/entrega/{id}/status
Content-Type: application/json

{
  "novoStatus": "A_CAMINHO"
}
```

#### **Finalizar Entrega**
```http
POST /api/entrega/{id}/finalizar
```

#### **Cliente Avalia**
```http
POST /api/avaliacao/cliente
Content-Type: application/json

{
  "solicitacaoId": 1,
  "nota": 5,
  "comentario": "Excelente serviço!"
}
```

#### **Recebedor Avalia**
```http
POST /api/avaliacao/recebedor
Content-Type: application/json

{
  "solicitacaoId": 1,
  "nota": 4,
  "comentario": "Entrega dentro do prazo"
}
```

#### **Confirmar Pagamento**
```http
POST /api/pagamento/{id}/confirmar
Content-Type: application/json

{
  "transacaoId": "TXN_123456789"
}
```

---

## 📂 Estrutura do Projeto

```
src/main/java/com/example/frota/
├── api/                          # API REST
│   └── MobileApiController.java
├── avaliacao/                    # Sistema de Avaliações
│   ├── Avaliacao.java
│   ├── AvaliacaoRepository.java
│   ├── AvaliacaoService.java
│   └── [DTOs]
├── caixaPadronizada/            # Caixas Padronizadas
│   ├── CaixaPadronizada.java
│   ├── CaixaPadronizadaController.java
│   ├── CaixaPadronizadaService.java
│   └── [DTOs]
├── caminhao/                    # Gestão de Caminhões
│   ├── Caminhao.java
│   ├── CaminhaoController.java
│   ├── CaminhaoService.java
│   └── [DTOs]
├── enums/                       # Enumerações
│   ├── StatusEntrega.java
│   ├── StatusPagamento.java
│   └── TipoManutencao.java
├── manutencao/                  # Sistema de Manutenção
│   ├── Manutencao.java
│   ├── ManutencaoController.java
│   ├── ManutencaoService.java   # ⚠️ ALERTAS AUTOMÁTICOS
│   └── [DTOs]
├── motorista/                   # Gestão de Motoristas
│   ├── Motorista.java
│   ├── MotoristaController.java
│   ├── MotoristaService.java
│   └── [DTOs]
├── otimizacao/                  # Otimização de Carga
│   ├── OtimizacaoCargaService.java  # 🎯 ALGORITMO
│   └── OtimizacaoController.java
├── pagamento/                   # Sistema de Pagamento
│   ├── Pagamento.java
│   ├── PagamentoService.java
│   └── [DTOs]
├── percurso/                    # Controle de Viagens
│   ├── Percurso.java
│   ├── PercursoService.java
│   └── [DTOs]
└── solicitacaoTransporte/       # Solicitações
    ├── SolicitacaoTransporte.java
    ├── SolicitacaoController.java
    ├── FreightService.java      # 💰 CÁLCULO DE FRETE
    └── [DTOs]

src/main/resources/
├── templates/                   # Templates HTML
│   ├── fragmentos/
│   │   ├── header.html
│   │   ├── footer.html
│   │   └── head.html
│   ├── motorista/
│   │   ├── cadastrar.html
│   │   └── mostrar.html
│   ├── manutencao/
│   │   ├── cadastrar.html
│   │   ├── mostrar.html
│   │   └── alertas.html         # 🔴 ALERTAS
│   ├── otimizacao/
│   │   └── dashboard.html       # 📊 DASHBOARD
│   ├── solicitacao/
│   │   ├── cadastrar.html
│   │   ├── mostrar.html
│   │   └── mostrar-detalhe.html
│   └── caixa/
│       ├── cadastrar.html
│       └── mostrar.html
└── application.properties       # Configurações
```

---

## 📚 Documentação

### Documentos Disponíveis

- **[ROTAS_API_POSTMAN.md](ROTAS_API_POSTMAN.md)** - Guia completo das APIs REST
- **[RESUMO_ROTAS_REST.md](RESUMO_ROTAS_REST.md)** - Resumo executivo das 66+ APIs
- **[Frotalux_Postman_Collection.json](Frotalux_Postman_Collection.json)** - Coleção do Postman
- **[IMPLEMENTACAO.md](IMPLEMENTACAO.md)** - Documentação da Parte 1 (Cubagem e Frete)
- **[IMPLEMENTACAO_PARTE2.md](IMPLEMENTACAO_PARTE2.md)** - Documentação da Parte 2 (Sistema Completo)
- **[GUIA_INICIALIZACAO.md](GUIA_INICIALIZACAO.md)** - Guia de inicialização
- **[ARQUITETURA_DUAL.md](ARQUITETURA_DUAL.md)** - Arquitetura Web + API
- **[GUIA_ESTILIZACAO.md](GUIA_ESTILIZACAO.md)** - Guia de estilos do frontend

### Cálculo de Frete

#### Fórmula do Peso Cubado
```
Peso Cubado = Volume (m³) × Fator de Cubagem (300 kg/m³)
Volume = Comprimento × Largura × Altura
```

#### Peso Considerado
```
Peso Considerado = MAX(Peso Real, Peso Cubado)
```

#### Composição do Frete
```
Custo por Peso = Peso Considerado × Taxa/kg/km × Distância
Custo por Caixa = Taxa/caixa/km × Distância
Custo Base = MIN(Custo por Peso, Custo por Caixa)
Custo Km Rodado = Taxa/km × Distância
Frete Total = Custo Base + Custo Km Rodado + Pedágio
```

### Configurações

#### application.properties
```properties
# Banco de Dados
spring.datasource.url=jdbc:mysql://localhost:3306/frota
spring.datasource.username=root
spring.datasource.password=senha

# Servidor
server.port=8083

# Rotas e APIs
routing.provider=haversine
routing.ors.apiKey=
routing.toll.perKm=0.05

# Preços de Frete
freight.rate.perKmPerBox=2.0
freight.rate.perKgPerKm=0.01
freight.rate.perKm=1.5
```

---

## 🎨 Screenshots

### Dashboard de Otimização
![Dashboard](docs/images/dashboard.png)

### Alertas de Manutenção
![Alertas](docs/images/alertas.png)

### Solicitação de Transporte
![Solicitação](docs/images/solicitacao.png)

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

---

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👨‍💻 Autores

- **Beatriz Silva** - *Desenvolvimento e Implementação*

---

## 🙏 Agradecimentos

- Spring Boot Framework
- Comunidade Java
- Bootstrap Team
- FontAwesome

---

## 📞 Contato

Para dúvidas ou sugestões, entre em contato:

- Email: contato@frotalux.com
- GitHub: [@seu-usuario](https://github.com/seu-usuario)

---

<div align="center">

**Desenvolvido com ❤️ usando Spring Boot**

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?style=for-the-badge&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)

</div>
