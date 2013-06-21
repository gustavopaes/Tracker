var http = require("http")
, mongo = require("mongodb")
, qs = require("querystring")
, port = 8084;

var server = http.createServer(function(request, response){

	console.log("\n[start] ", new Date());

	// TO-DO
	// Esse servidor irá apenas receber um request do APP
	// com o e-mail da conta e as coordenadas que o APP acabou armazenando
	// enquanto não possuia conexão para enviar.

	// Os dados devem ser armazenados, do jeito que vieram do APP, em um
	// banco de dados MongoDB.

	// Se possível, fazer o armazenamento de forma asyncrona, para que esse
	// servidor possa mandar status 200 OK o mais rápido possível para o APP.

}).listen(port);

console.log('Iniciando NodeJS Server na porta', port);
