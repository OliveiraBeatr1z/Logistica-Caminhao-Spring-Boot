# Frota — Cubagem e Cálculo de Frete

Este projeto foi estendido para suportar cálculo de cubagem e uma lógica inicial de cálculo de frete.

O que foi adicionado / alterado
------------------------------
- Projeto configurado para Java 21 no `pom.xml`.
- Entidade `Caminhao` agora contém dimensões: `comprimento`, `largura`, `altura` (metros), e `fatorCubagem` (kg/m³). Também foram adicionados os métodos derivados `getVolume()` e `getPesoCubado()`.
- Entidade `CaixaPadronizada` criada (dimensões, material, limitePeso) com repositório.
- Entidade `SolicitacaoTransporte` criada para representar um pedido de transporte (produto, dimensões, peso real, origem/destino geográficos).
- Serviço `FreightService` que:
  - Calcula volume e peso cubado: Peso Cubado = Volume (m³) × Fator de Cubagem (padrão rodoviário 300 kg/m³).
  - Compara peso real com peso cubado e considera para cobrança o maior dos dois.
  - Permite cobrança por caixa (se o produto couber em uma das caixas padronizadas) ou por peso, e estima pedágio através de um `TollClient`.
- Interfaces e implementações para distância/pedágio:
  - `DistanceClient` (abstração) com implementação de fallback `HaversineDistanceClient`.
  - `OpenRouteServiceClient` (esqueleto) que pode ser ativado via `routing.provider=ors` e `routing.ors.apiKey`.
  - `TollClient` com uma implementação placeholder (`PlaceholderTollClient`) que usa um valor por km configurável.
- Endpoints REST novos:
  - POST `/solicitacoes/cotacao` — Retorna `{ "valor": number }` com o valor do frete estimado.
  - POST `/solicitacoes` — Persiste a solicitação e retorna `{ "id": ..., "valor": ... }`.

Configuração
------------
Edite `src/main/resources/application.properties` para definir credenciais do banco e chaves de API caso necessário.

Propriedades principais adicionadas:

- `routing.provider` — `haversine` (padrão) ou `ors` (OpenRouteService).
- `routing.ors.apiKey` — chave da OpenRouteService (opcional). Se não fornecida, o fallback Haversine será usado.
- `routing.toll.perKm` — taxa por km usada pelo `PlaceholderTollClient` quando nenhum provedor de pedágio estiver configurado.
- `freight.rate.perKmPerBox` — exemplo de preço por caixa por km.
- `freight.rate.perKgPerKm` — exemplo de preço por kg por km.

Como executar
-------------
Verifique se você tem um JDK instalado (o projeto mira Java 21). O Maven wrapper usará o JDK do sistema.

No PowerShell (Windows):

```powershell
cd c:\Users\bea47\Downloads\frota\frota
.\mvnw -DskipTests package
.\mvnw spring-boot:run
```

Se quiser garantir o uso do JDK 21, defina `JAVA_HOME` apontando para sua instalação do JDK 21 antes de executar os comandos.

Exemplo de requisição para cotação (POST `/solicitacoes/cotacao`):

```json
{
  "produto": "Tênis",
  "comprimento": 0.3,
  "largura": 0.2,
  "altura": 0.15,
  "pesoReal": 1.2,
  "origemLat": -23.550520,
  "origemLon": -46.633308,
  "destinoLat": -22.903539,
  "destinoLon": -43.209587
}
```

APIs externas e observações
---------------------------
- OpenRouteService (ORS): integração opcional. Ao definir `routing.provider=ors` e fornecer `routing.ors.apiKey`, o cliente ORS tentará obter a rota. A resposta do ORS precisa ser parseada para extrair a distância exata (implementação deixada como esqueleto nesta etapa — o cliente já realiza a chamada e faz fallback quando necessário).

- Pedágio: o cálculo preciso de pedágio exige APIs específicas (Google Routes, HERE, Mapplus, ou serviços pagos do ORS). Nesta entrega incluí uma abstração `TollClient` e um `PlaceholderTollClient` que estima o pedágio por km. Posso implementar um `TollClient` para um provedor real se você fornecer a escolha e/ou as chaves.

O que não foi alterado
---------------------
- Não atualizei o Spring Boot (mantive 3.5.5). Se desejar, posso preparar um plano de atualização de dependências.
- A implementação completa de parsing de resposta do ORS e um `TollClient` de produção dependem de chaves e do formato de resposta do provedor — posso concluir assim que você indicar o provedor e fornecer as chaves (ou autorizar a leitura de variáveis de ambiente).

Próximos passos possíveis (me diga qual prefere)
-----------------------------------------------
- Implementar parsing completo do OpenRouteService para usar distância real de rota e (quando disponível) estimativas de pedágio.
- Implementar client para HERE ou Google Routes para distâncias e pedágios (requer chaves).
- Integrar as novas APIs na interface (ex.: escolher provedor via `routing.provider`) e adicionar fallback.
- Atualizar as páginas Thymeleaf para permitir criação de solicitação via UI e mostrar opções de caixas e cotação.

Se quiser que avance em qualquer uma dessas tarefas, diga qual delas e eu implemento.
