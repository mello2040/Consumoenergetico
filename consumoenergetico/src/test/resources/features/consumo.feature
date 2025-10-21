# language: pt
Funcionalidade: Gestão de Consumo Energético (ESG)
  Como usuário da API
  Quero gerenciar registros de consumo energético
  Para garantir qualidade, conformidade e rastreabilidade

  Contexto:
    Dado que a baseUrl está configurada

  @positivo
  Cenário: Cadastrar consumo válido (caminho feliz)
    Quando eu faço POST para "/consumo" com o payload "consumo-valido.json"
    Então o status da resposta deve ser 201
    E a resposta deve obedecer o schema "consumo-exibicao-schema.json"
    E eu salvo o "id" retornado

  @positivo
  Cenário: Listar consumos paginados
    Quando eu faço GET para "/consumo"
    Então o status da resposta deve ser 200
    E a resposta deve obedecer o schema "page-consumo-schema.json"

  @negativo
  Cenário: Impedir cadastro com unidade em branco
    Dado que a baseUrl está configurada
    Quando eu faço POST para "/consumo" com o payload "consumo-invalido-unidade-vazia.json"
    Então o status da resposta deve ser 400

  @positivo
  Cenário: Atualizar consumo existente
    Dado que eu tenho um consumo cadastrado via "consumo-valido.json"
    Quando eu faço PUT para "/consumo" com o payload "consumo-update.json" (injetando o id salvo)
    Então o status da resposta deve ser 200

  @positivo
  Cenário: Remover consumo existente
    Dado que eu tenho um consumo cadastrado via "consumo-valido.json"
    Quando eu faço DELETE para "/consumo/{id-salvo}"
    Então o status da resposta deve ser 204

  # Exemplo no seu formato (com tabela de dados)
  @positivo @tabela
  Cenário: Cadastro bem-sucedido de consumo via tabela
    Dado que eu tenha os seguintes dados do consumo:
      | campo      | valor      |
      | qtdConsumo | 123.45     |
      | data       | 2025-10-01 |
      | unidade    | kWh        |
    Quando eu enviar a requisição para o endpoint "/consumo" de cadastro de consumos
    Então o status da resposta deve ser 201
    E a resposta deve obedecer o schema "consumo-exibicao-schema.json"
