# 🚀 Guia de Inicialização do Sistema de Frota

## Pré-requisitos

✅ **Java 17+** instalado  
✅ **MySQL** instalado e rodando  
✅ **Maven** instalado (ou usar o Maven Wrapper incluído)

## Passo a Passo

### 1️⃣ Iniciar o MySQL

Certifique-se de que o MySQL está rodando na porta **3306**

```bash
# Verificar se o MySQL está rodando
mysql -u root -p
```

### 2️⃣ Configurar Credenciais (se necessário)

Edite o arquivo `src/main/resources/application.properties` se suas credenciais forem diferentes:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/frota?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=8250  # <-- Altere se necessário
```

**Observação**: O banco de dados `frota` será criado automaticamente na primeira execução!

### 3️⃣ Iniciar o Projeto

**Opção A - Via Maven Wrapper (recomendado):**
```bash
cd /Users/beatriz.silva/Documents/faculdade/eng4/Logistica-Caminhao-Spring-Boot
./mvnw spring-boot:run
```

**Opção B - Via Maven instalado:**
```bash
mvn spring-boot:run
```

**Opção C - Via IDE (Eclipse/IntelliJ):**
- Abra o projeto na IDE
- Execute a classe `FrotaApplication.java` como "Spring Boot App"

### 4️⃣ Acessar o Sistema

Após iniciar, o sistema estará disponível em:

🌐 **http://localhost:8083**

## 📋 Endpoints Disponíveis

### Interface Web (Thymeleaf)

#### Caminhões
- `GET http://localhost:8083/caminhoes` - Listar caminhões
- `GET http://localhost:8083/caminhao/novo` - Cadastrar novo caminhão

#### Marcas
- `GET http://localhost:8083/marcas` - Listar marcas
- `GET http://localhost:8083/marca/novo` - Cadastrar nova marca

#### Caixas Padronizadas ✨ **NOVO**
- `GET http://localhost:8083/caixas/cadastrar` - Cadastrar caixa
- `GET http://localhost:8083/caixas/mostrar` - Listar caixas
- `GET http://localhost:8083/caixas/mostrar/{id}` - Detalhes da caixa
- `GET http://localhost:8083/caixas/atualizar/{id}` - Editar caixa

#### Solicitações de Transporte ✨ **NOVO**
- `GET http://localhost:8083/solicitacoes/cadastrar` - Solicitar transporte
- `GET http://localhost:8083/solicitacoes/mostrar` - Listar solicitações
- `GET http://localhost:8083/solicitacoes/mostrar/{id}` - **Detalhes com cálculo completo**
- `GET http://localhost:8083/solicitacoes/atualizar/{id}` - Editar solicitação

### API REST

#### Cotação de Frete
```bash
POST http://localhost:8083/api/solicitacoes/cotacao
Content-Type: application/json

{
  "produto": "Equipamento Eletrônico",
  "comprimento": 0.5,
  "largura": 0.4,
  "altura": 0.3,
  "pesoReal": 20.0,
  "origemLat": -23.5505,
  "origemLon": -46.6333,
  "destinoLat": -22.9068,
  "destinoLon": -43.1729,
  "origemEndereco": "São Paulo, SP",
  "destinoEndereco": "Rio de Janeiro, RJ"
}
```

#### Criar Solicitação
```bash
POST http://localhost:8083/api/solicitacoes
Content-Type: application/json

{
  "produto": "Equipamento Eletrônico",
  "comprimento": 0.5,
  "largura": 0.4,
  "altura": 0.3,
  "pesoReal": 20.0,
  "origemLat": -23.5505,
  "origemLon": -46.6333,
  "destinoLat": -22.9068,
  "destinoLon": -43.1729,
  "origemEndereco": "São Paulo, SP",
  "destinoEndereco": "Rio de Janeiro, RJ",
  "caixaId": 1,
  "caminhaoId": 1
}
```

## 🎯 Fluxo de Uso Recomendado

### 1. Primeiro Uso - Cadastrar Dados Base

1. **Cadastrar Marcas** (ex: Volvo, Scania, Mercedes)
2. **Cadastrar Caminhões** com dimensões completas
3. **Cadastrar Caixas Padronizadas** (diferentes tamanhos)

### 2. Solicitar Transporte

1. Acesse: `http://localhost:8083/solicitacoes/cadastrar`
2. Preencha os dados do produto:
   - Nome do produto
   - Dimensões (em metros)
   - Peso real (em kg)
3. Preencha origem e destino:
   - Endereços legíveis
   - Coordenadas (latitude/longitude)
4. Opcionalmente selecione caixa e caminhão
5. Clique em "Calcular Frete e Solicitar"

### 3. Visualizar Detalhes do Frete

Na tela de detalhes da solicitação, você verá:
- ✅ Peso Real vs Peso Cubado
- ✅ Peso Considerado (o maior)
- ✅ Breakdown completo do cálculo:
  - Distância calculada
  - Custo por peso
  - Custo por caixa
  - Custo base (menor dos dois)
  - Custo por km rodado
  - Valor do pedágio
  - **Valor Total do Frete**

## ⚙️ Configurações Personalizáveis

Edite `application.properties` para ajustar as taxas:

```properties
# Taxa por km por caixa (R$)
freight.rate.perKmPerBox=2.0

# Taxa por kg por km (R$)
freight.rate.perKgPerKm=0.01

# Taxa por km rodado (R$)
freight.rate.perKm=1.5

# Taxa de pedágio por km (R$)
routing.toll.perKm=0.05
```

## 🐛 Troubleshooting

### Erro de Conexão com MySQL
```
Error: Could not connect to database
```
**Solução**: Verifique se o MySQL está rodando e as credenciais estão corretas.

### Porta 8083 já em uso
```
Error: Port 8083 is already in use
```
**Solução**: Altere a porta no `application.properties`:
```properties
server.port=8084
```

### Tabelas não são criadas
**Solução**: Verifique se o `ddl-auto` está configurado:
```properties
spring.jpa.hibernate.ddl-auto=update
```

## 📊 Estrutura do Banco de Dados

O Hibernate criará automaticamente as seguintes tabelas:

- `marca` - Marcas de caminhões
- `caminhao` - Caminhões com dimensões
- `caixa_padronizada` - Caixas padronizadas ✨ **NOVA**
- `solicitacao_transporte` - Solicitações de transporte ✨ **NOVA**

## 🧪 Testando a API

### Usando cURL

```bash
# Cotação de frete
curl -X POST http://localhost:8083/api/solicitacoes/cotacao \
  -H "Content-Type: application/json" \
  -d '{
    "produto": "Notebook",
    "comprimento": 0.4,
    "largura": 0.3,
    "altura": 0.05,
    "pesoReal": 2.5,
    "origemLat": -23.5505,
    "origemLon": -46.6333,
    "destinoLat": -22.9068,
    "destinoLon": -43.1729,
    "origemEndereco": "São Paulo",
    "destinoEndereco": "Rio de Janeiro"
  }'
```

### Usando Postman/Insomnia

Importe a coleção de requisições ou crie manualmente usando os exemplos acima.

## 📈 Logs e Debug

Para ativar logs detalhados, descomente no `application.properties`:

```properties
logging.level.org.springframework=DEBUG
logging.level.com.example.frota=TRACE
```

## ✅ Checklist de Inicialização

- [ ] MySQL rodando na porta 3306
- [ ] Credenciais do banco configuradas
- [ ] Projeto compilado com sucesso
- [ ] Servidor iniciado na porta 8083
- [ ] Tabelas criadas automaticamente
- [ ] Interface acessível no navegador
- [ ] Cadastrar marcas e caminhões
- [ ] Cadastrar caixas padronizadas
- [ ] Criar primeira solicitação de transporte
- [ ] Verificar cálculo de frete

## 🎉 Pronto para Usar!

Agora você tem um sistema completo de gestão de frota com cálculo inteligente de frete baseado em cubagem!

