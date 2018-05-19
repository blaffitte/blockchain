package me.benzo.db.blockchain.rest.impl;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.mashape.unirest.http.exceptions.UnirestException;

import me.benzo.db.blockchain.model.Block;
import me.benzo.db.blockchain.model.Transaction;
import me.benzo.db.blockchain.rest.BlockChainAPI;
import me.benzo.db.blockchain.service.impl.BlockChain;

@Component
public class BlockChainController implements BlockChainAPI {
	
	private BlockChain blockchain;
	
	@Autowired
	public BlockChainController(BlockChain blockchain) {
		super();
		this.blockchain = blockchain;
	}

	// web server calls
	/* (non-Javadoc)
	 * @see me.benzo.db.blockchain.BlockChainAPI#mine()
	 */
	@Override
	public Callable<ResponseEntity<?>> mine() throws NoSuchAlgorithmException {
		return () -> {
			int proof = this.blockchain.createProofOfWork(this.blockchain.getLastBlock().getProof(), this.blockchain.getLastBlock().getPreviousHash());

			createTransaction(new Transaction(blockchain.getNodeId(), "0", ""));
			Block block = this.blockchain.createNewBlock(proof, null /* , _getLastBlock().PreviousHash */);
			this.blockchain.setLastBlock(block);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "New Block Forged");
			response.put("index", block.getIndex());
			response.put("transactions", block.getTransactions());
			response.put("proof", block.getProof());
			response.put("previousHash", block.getPreviousHash());

			return ResponseEntity.ok(response);
		};
	}

	/* (non-Javadoc)
	 * @see me.benzo.db.blockchain.BlockChainAPI#fullChain()
	 */
	@Override
	public ResponseEntity<?> fullChain() {
		Map<String, Object> response = new HashMap<>();
		response.put("chain", this.blockchain.getChain());
		response.put("length", this.blockchain.getChain().size());
		return ResponseEntity.ok(response);
	}
	
	/* (non-Javadoc)
	 * @see me.benzo.db.blockchain.BlockChainAPI#whoami()
	 */
	@Override
	public ResponseEntity<?> whoami(){
		return ResponseEntity.ok(this.blockchain.getNodeId());
	}

	/* (non-Javadoc)
	 * @see me.benzo.db.blockchain.BlockChainAPI#listNodes()
	 */
	@Override
	public ResponseEntity<?> listNodes(){
		return ResponseEntity.ok(this.blockchain.getNodes());
	}
	
	@Override
	public String registerNodes(@RequestBody String[] nodes) throws MalformedURLException {
		StringBuilder builder = new StringBuilder();
		for (String node : nodes) {
			String url = String.format("http://%s", node);
			this.blockchain.registerNode(url);
			builder.append(String.format("%s, ", url));
		}

		builder.insert(0, String.format("%d new nodes have been added: ", nodes.length));
		String result = builder.toString();
		return result.substring(0, result.length() - 2);

	}

	/* (non-Javadoc)
	 * @see me.benzo.db.blockchain.BlockChainAPI#consensus()
	 */
	@Override
	public ResponseEntity<?> consensus() throws MalformedURLException, NoSuchAlgorithmException, UnirestException {
		boolean replaced = this.blockchain.resolveConflicts();
		String message = replaced ? "was replaced" : "is authoritive";

		Map<String, Object> response = new HashMap<>();
		response.put("message", String.format("Our chain %s", message));
		response.put("chain", JSONObject.valueToString(this.blockchain.getChain()));

		return ResponseEntity.ok(response);
	}

	/* (non-Javadoc)
	 * @see me.benzo.db.blockchain.BlockChainAPI#createTransaction(me.benzo.db.blockchain.Transaction)
	 */
	@Override
	public ResponseEntity<?> createTransaction(@RequestBody Transaction data) {
		this.blockchain.getCurrentTransactions().add(data);

		return ResponseEntity.ok(this.blockchain.getLastBlock() != null ? this.blockchain.getLastBlock().getIndex() + 1 : 0);
	}

}