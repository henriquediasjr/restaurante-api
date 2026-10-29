#!/usr/bin/env bash
# Roteiro de demonstracao da API para o video/apresentacao.
# Uso: suba o servidor (./gradlew bootRun) e rode ./scripts/demo.sh
# Defina NOPAUSE=1 para executar sem pausas entre os passos.
U=${API_URL:-http://localhost:8080/api}
J='Content-Type: application/json'

step() { echo; echo "=== $1"; [ -z "$NOPAUSE" ] && read -r -p "(enter para executar) " _; }
call() { # call <metodo> <rota> [token] [corpo]
  local args=(-s -X "$1" "$U$2" -w '\n-> HTTP %{http_code}\n')
  [ -n "$3" ] && args+=(-H "Authorization: Bearer $3")
  [ -n "$4" ] && args+=(-H "$J" -d "$4")
  echo "\$ $1 $2 ${4:+$4}"
  curl "${args[@]}"
}
login() { curl -s -X POST "$U/users/login" -H "$J" -d "{\"email\":\"$1\",\"password\":\"$2\"}" \
  | python3 -c "import sys,json;print(json.load(sys.stdin)['token'])"; }

echo "################ PARTE 1 - Henrique: CRUD e relacao 1-N ################"
step "Login do ADMIN (criado no bootstrap)"
ADMIN=$(login admin@authserver.com admin); echo "token: ${ADMIN:0:40}..."

step "Criar categorias (POST /categories)"
call POST /categories "$ADMIN" '{"name":"Pratos","description":"Pratos principais"}'
call POST /categories "$ADMIN" '{"name":"Bebidas","description":"Bebidas geladas"}'

step "Criar produtos ligados a uma categoria (POST /products)"
call POST /products "$ADMIN" '{"name":"Risoto de cogumelos","description":"Arroz arboreo","price":42.50,"categoryId":1}'
call POST /products "$ADMIN" '{"name":"Lasanha","description":"Bolonhesa","price":38.00,"categoryId":1}'
call POST /products "$ADMIN" '{"name":"Suco de laranja","description":"Natural","price":9.00,"categoryId":2}'

step "Ler, atualizar (PUT) e listar produtos"
call GET /products/1
call PUT /products/3 "$ADMIN" '{"name":"Suco de laranja","description":"Natural 500ml","price":10.00,"categoryId":2}'

step "Validacao: preco negativo -> 422"
call POST /products "$ADMIN" '{"name":"Ruim","description":"x","price":-1,"categoryId":1}'

step "Apagar categoria com produtos -> 409 Conflict"
call DELETE /categories/1 "$ADMIN"

echo; echo "################ PARTE 2 - Lucas: associacoes e consulta ################"
step "Criar tag e associar ao produto (N-N Product x Tag)"
call POST /tags "$ADMIN" '{"name":"vegano"}'
call PUT /products/1/tags/vegano "$ADMIN"
call PUT /products/1/tags/vegano "$ADMIN"   # repetido -> 204, nada muda
call GET /products/1

step "Criar pedido e adicionar produtos (N-N Order x Product)"
call POST /orders "$ADMIN" '{"userId":1}'
call PUT /orders/1/products/1 "$ADMIN"
call PUT /orders/1/products/3 "$ADMIN"
call GET /orders/1 "$ADMIN"

step "Remover produto do pedido (total recalculado)"
call DELETE /orders/1/products/3 "$ADMIN"
call GET /orders/1 "$ADMIN"

step "Consulta com filtros e ordenacao por query params"
call GET "/products?tag=vegano"
call GET "/products?maxPrice=40&sortBy=price&sortDir=desc"
call GET "/products?categoryId=1&name=lasa"
call GET "/products?sortBy=cor"

echo; echo "################ PARTE 3 - Julia: seguranca e erros ################"
step "Login com senha errada -> 401 (e log.warn no servidor)"
call POST /users/login "" '{"email":"admin@authserver.com","password":"errada"}'

step "Rota protegida sem token -> 401"
call POST /categories "" '{"name":"X","description":"y"}'

step "Usuario comum: cria conta, loga e tenta apagar -> 403"
call POST /users "" '{"email":"cliente@email.com","password":"1234","name":"Cliente"}'
COMUM=$(login cliente@email.com 1234)
call DELETE /products/3 "$COMUM"

step "ADMIN apaga -> 204"
call DELETE /products/3 "$ADMIN"
call GET /products/3

echo; echo "Fim da demo. Agora mostre os logs no console e rode: ./gradlew test"
