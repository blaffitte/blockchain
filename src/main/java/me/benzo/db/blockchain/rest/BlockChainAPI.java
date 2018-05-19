package me.benzo.db.blockchain.rest;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.mashape.unirest.http.exceptions.UnirestException;

import me.benzo.db.blockchain.model.Transaction;

@Produces("application/json")
public interface BlockChainAPI {

	// web server calls
	@Path("/mine")
	@POST
	Response mine() throws NoSuchAlgorithmException;

	@Path("/chain")
	@GET
	Response fullChain();

	@Path("/nodes/whoami")
	@GET
	Response whoami();

	@Path("/nodes")
	@GET
	Response listNodes();

	@Path("/nodes/resolve")
	@POST
	Response consensus() throws MalformedURLException, NoSuchAlgorithmException, UnirestException;

	@Path("/transactions")
	@POST
	Response createTransaction(Transaction data);

	@Path("/nodes/register")
	@POST
	@Consumes("application/json")
	@Produces("test/plain")
	Response registerNodes(String[] nodes) throws MalformedURLException;

}