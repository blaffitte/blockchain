package me.benzo.db.blockchain.rest;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.exceptions.UnirestException;

import me.benzo.db.blockchain.model.Transaction;

@RestController()
@RequestMapping(produces = "application/json")
public interface BlockChainAPI {

	// web server calls
	@RequestMapping(value = "/mine", method = RequestMethod.POST)
	Callable<ResponseEntity<?>> mine() throws NoSuchAlgorithmException;

	@RequestMapping(value = "/chain", method = RequestMethod.GET)
	ResponseEntity<?> fullChain();

	@RequestMapping(value = "/nodes/whoami", method = RequestMethod.GET)
	ResponseEntity<?> whoami();

	@RequestMapping(value = "/nodes", method = RequestMethod.GET)
	ResponseEntity<?> listNodes();

	@RequestMapping(value = "/nodes/resolve", method = RequestMethod.POST)
	ResponseEntity<?> consensus() throws MalformedURLException, NoSuchAlgorithmException, UnirestException;
	
	@RequestMapping(value = "/transactions", method = RequestMethod.POST)
	ResponseEntity<?> createTransaction(Transaction data);
	
	@RequestMapping(value = "/nodes/register", method = RequestMethod.POST, produces = "text/plain", consumes = "application/json")
	String registerNodes(String[] nodes) throws MalformedURLException;

}