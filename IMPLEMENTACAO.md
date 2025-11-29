# Sistema de Gestão de Frota e Cálculo de Frete - Implementação Completa

## Visão Geral

Este projeto implementa um sistema completo de gestão de frota e cálculo de frete para transporte rodoviário, incluindo o conceito de **cubagem** e otimização de custos.

## Funcionalidades Implementadas

### 1. Gestão de Caminhões

A entidade `Caminhao` foi expandida com os seguintes campos:

- **Dimensões**: Comprimento, Largura e Altura (em metros)
- **Fator de Cubagem**: 300 kg/m³ (padrão para transporte rodoviário)
- **Métodos Derivados**:
  - `getVolume()`: Calcula o volume em m³
  - `getPesoCubado()`: Calcula o peso cubado (Volume × Fator de Cubagem)

### 2. Gestão de Caixas Padronizadas

Nova entidade `CaixaPadronizada` com:

- Material (tipo de papelão)
- Dimensões (Comprimento, Largura, Altura em metros)
- Limite de Peso (kg)
- Volume calculado automaticamente

**Endpoints**:
- `GET /caixas/cadastrar` - Formulário de cadastro
- `POST /caixas/cadastrar` - Salvar caixa
- `GET /caixas/mostrar` - Listar todas as caixas
- `GET /caixas/mostrar/{id}` - Detalhes da caixa
- `GET /caixas/atualizar/{id}` - Formulário de edição
- `POST /caixas/atualizar/{id}` - Atualizar caixa
- `POST /caixas/deletar/{id}` - Excluir caixa

### 3. Solicitação de Transporte

Nova entidade `SolicitacaoTransporte` com cálculo completo de frete:

**Dados do Produto**:
- Nome do produto
- Dimensões (Comprimento, Largura, Altura)
- Peso Real (kg)

**Origem e Destino**:
- Endereços legíveis
- Coordenadas geográficas (latitude/longitude)

**Recursos Opcionais**:
- Caixa Padronizada (selecionada ou automática)
- Caminhão (selecionado ou automático)

**Dados Calculados**:
- Distância em km
- Peso Cubado
- Peso Considerado (maior entre peso real e peso cubado)
- Valor do Frete
- Valor do Pedágio

**Endpoints**:
- `GET /solicitacoes/cadastrar` - Formulário de solicitação
- `POST /solicitacoes/cadastrar` - Criar solicitação e calcular frete
- `GET /solicitacoes/mostrar` - Listar todas as solicitações
- `GET /solicitacoes/mostrar/{id}` - Detalhes com breakdown do cálculo
- `GET /solicitacoes/atualizar/{id}` - Formulário de edição
- `POST /solicitacoes/atualizar/{id}` - Atualizar e recalcular
- `POST /solicitacoes/deletar/{id}` - Excluir solicitação

**API REST**:
- `POST /api/solicitacoes/cotacao` - Cotação sem salvar
- `POST /api/solicitacoes` - Criar solicitação via API

## Cálculo de Frete

### Fórmula do Peso Cubado

```
Peso Cubado = Volume (m³) × Fator de Cubagem (300 kg/m³)
Volume = Comprimento × Largura × Altura
```

### Peso Considerado

O sistema sempre usa o **maior valor** entre:
- Peso Real da carga
- Peso Cubado calculado

### Composição do Frete

O valor final do frete é composto por:

1. **Custo Base** (menor entre):
   - Custo por Peso: `Peso Considerado × Taxa/kg/km × Distância`
   - Custo por Caixa: `Taxa/caixa/km × Distância` (se aplicável)

2. **Custo por Km Rodado**: `Taxa/km × Distância`

3. **Pedágio**: Calculado via API de pedágios

**Fórmula Final**:
```
Frete Total = Custo Base + Custo Km Rodado + Pedágio
```

### Taxas Configuráveis

As taxas podem ser configuradas no `application.properties`:

```properties
# Taxa por km por caixa (R$)
freight.rate.perKmPerBox=2.0

# Taxa por kg por km (R$)
freight.rate.perKgPerKm=0.01

# Taxa por km rodado (R$)
freight.rate.perKm=1.5
```

## Integração com APIs

### APIs de Distância

O sistema suporta múltiplas APIs de cálculo de distância através da interface `DistanceClient`:

- **HaversineDistanceClient** (implementado): Cálculo de distância em linha reta
- **OpenRouteServiceClient** (stub): Rotas reais considerando estradas
- Preparado para Google Maps, Bing Maps, etc.

### APIs de Pedágio

Suporte para APIs de cálculo de pedágio através da interface `TollClient`:

- **PlaceholderTollClient** (implementado): Estimativa básica
- Preparado para Google Routes, HERE Technologies, Mapplus, OpenRouteService

## Estrutura do Projeto

```
com.example.frota/
├── caminhao/
│   ├── Caminhao.java (entidade atualizada)
│   ├── CaminhaoService.java
│   ├── CaminhaoController.java
│   ├── CadastroCaminhao.java
│   └── AtualizacaoCaminhao.java
├── caixaPadronizada/
│   ├── CaixaPadronizada.java
│   ├── CaixaPadronizadaRepository.java
│   ├── CaixaPadronizadaService.java
│   ├── CaixaPadronizadaController.java
│   ├── DadosCadastroCaixa.java
│   ├── DadosAtualizacaoCaixa.java
│   └── DadosListagemCaixa.java
└── solicitacaoTransporte/
    ├── SolicitacaoTransporte.java
    ├── SolicitacaoTransporteRepository.java
    ├── SolicitacaoTransporteService.java
    ├── SolicitacaoController.java
    ├── SolicitacaoApiController.java
    ├── FreightService.java
    ├── DistanceClient.java
    ├── HaversineDistanceClient.java
    ├── OpenRouteServiceClient.java
    ├── TollClient.java
    ├── PlaceholderTollClient.java
    ├── DadosCadastroSolicitacao.java
    ├── DadosAtualizacaoSolicitacao.java
    └── DadosListagemSolicitacao.java
```

## Templates HTML

### Caixas Padronizadas
- `templates/caixa/cadastrar.html`
- `templates/caixa/mostrar.html`
- `templates/caixa/mostrar-detalhe.html`
- `templates/caixa/atualizar.html`

### Solicitações de Transporte
- `templates/solicitacao/cadastrar.html`
- `templates/solicitacao/mostrar.html`
- `templates/solicitacao/mostrar-detalhe.html` (com breakdown detalhado do cálculo)
- `templates/solicitacao/atualizar.html`

### Caminhões (atualizado)
- `templates/caminhao/formulario.html` (incluindo campos de dimensões)

## Importância da Cubagem

### Otimização de Custos
As transportadoras usam o peso cubado para cobrar o frete de forma justa, pois:
- Cargas volumosas mas leves ocupam muito espaço
- Cargas pesadas mas pequenas ocupam pouco espaço
- O maior entre peso real e peso cubado garante rentabilidade

### Eficiência Logística
O cálculo ajuda a:
- Planejar o transporte adequadamente
- Usar o espaço do veículo de forma eficiente
- Evitar desperdício de capacidade

### Conformidade
- Evita multas e apreensão de carga
- Reduz custos com combustível
- Otimiza manutenção dos veículos

## Exemplo de Uso

### 1. Cadastrar uma Caixa Padronizada
```
Material: Papelão Ondulado
Dimensões: 1.0m × 0.8m × 0.6m
Limite de Peso: 100 kg
Volume calculado: 0.48 m³
```

### 2. Solicitar Transporte
```
Produto: Equipamento Eletrônico
Dimensões: 0.5m × 0.4m × 0.3m
Peso Real: 20 kg
Volume: 0.06 m³
Peso Cubado: 18 kg (0.06 × 300)
Peso Considerado: 20 kg (maior entre real e cubado)

Origem: São Paulo, SP
Destino: Rio de Janeiro, RJ
Distância: 430 km

Cálculo:
- Custo por Peso: 20 kg × R$ 0,01/kg/km × 430 km = R$ 86,00
- Custo por Caixa: R$ 2,00/km × 430 km = R$ 860,00
- Custo Base: R$ 86,00 (menor)
- Custo Km Rodado: R$ 1,50/km × 430 km = R$ 645,00
- Pedágio: R$ 50,00 (estimado)
- Frete Total: R$ 781,00
```

## Melhorias Futuras

1. **Integração Real com APIs**:
   - Google Maps Distance Matrix API
   - OpenRouteService completo
   - Google Routes API para pedágios

2. **Otimização de Rotas**:
   - Múltiplas paradas
   - Otimização de cargas
   - Agrupamento de entregas

3. **Relatórios e Dashboards**:
   - Análise de custos
   - Eficiência de rotas
   - Utilização de frota

4. **Sistema de Rastreamento**:
   - GPS em tempo real
   - Notificações de entrega
   - Histórico de viagens

## Tecnologias Utilizadas

- **Spring Boot** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Thymeleaf** - Template engine para HTML
- **Lombok** - Redução de boilerplate
- **Bootstrap** - CSS Framework
- **H2/MySQL** - Banco de dados

## Conclusão

Este sistema implementa uma solução completa para gestão de frota e cálculo de frete baseado em cubagem, seguindo as melhores práticas do transporte rodoviário brasileiro e oferecendo uma interface intuitiva para usuários finais.

