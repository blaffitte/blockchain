package me.benzo.db.blockchain.rest.impl;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.NoArgsConstructor;
import me.benzo.db.blockchain.model.Block;
import me.benzo.db.blockchain.model.Transaction;
import me.benzo.db.blockchain.rest.BlockChainAPI;
import me.benzo.db.blockchain.service.impl.BlockChain;

@Path("/")
@NoArgsConstructor
@ApplicationScoped
public class BlockChainImpl implements BlockChainAPI {

    @Inject
    private BlockChain blockchain;

    // web server calls
    /*
     * (non-Javadoc)
     * 
     * @see me.benzo.db.blockchain.BlockChainAPI#mine()
     */
    @Override
    public Response mine() throws NoSuchAlgorithmException {

        int proof = this.blockchain.createProofOfWork(this.blockchain.getLastBlock().getProof(),
                this.blockchain.getLastBlock().getPreviousHash());

        this.createTransaction(new Transaction(blockchain.getNodeId(), "0", ""));
        Block block = this.blockchain.createNewBlock(proof, null /* , _getLastBlock().PreviousHash */);
        this.blockchain.setLastBlock(block);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "New Block Forged");
        response.put("index", block.getIndex());
        response.put("transactions", block.getTransactions());
        response.put("proof", block.getProof());
        response.put("previousHash", block.getPreviousHash());

        return Response.ok(response).build();

    }

    /*
     * (non-Javadoc)
     * 
     * @see me.benzo.db.blockchain.BlockChainAPI#fullChain()
     */
    @Override
    public Response fullChain() {
        Map<String, Object> response = new HashMap<>();
        response.put("chain", this.blockchain.getChain());
        response.put("length", this.blockchain.getChain().size());
        return Response.ok(response).build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.benzo.db.blockchain.BlockChainAPI#whoami()
     */
    @Override
    public Response whoami() {
        return Response.ok(this.blockchain.getNodeId()).build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see me.benzo.db.blockchain.BlockChainAPI#listNodes()
     */
    @Override
    public Response listNodes() {
        return Response.ok(this.blockchain.getNodes()).build();
    }

    @Override
    public Response registerNodes(final String[] nodes) throws MalformedURLException {
        StringBuilder builder = new StringBuilder();
        for (String node : nodes) {
            String url = String.format("http://%s", node);
            this.blockchain.registerNode(url);
            builder.append(String.format("%s, ", url));
        }

        builder.insert(0, String.format("%d new nodes have been added: ", nodes.length));
        String result = builder.toString();
        return Response.ok(result.substring(0, result.length() - 2)).build();

    }

    /*
     * (non-Javadoc)
     * 
     * @see me.benzo.db.blockchain.BlockChainAPI#consensus()
     */
    @Override
    public Response consensus() throws MalformedURLException, NoSuchAlgorithmException, UnirestException {
        boolean replaced = this.blockchain.resolveConflicts();
        String message = replaced ? "was replaced" : "is authoritive";

        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("Our chain %s", message));
        response.put("chain", this.blockchain.getChain());

        return Response.ok(response).build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * me.benzo.db.blockchain.BlockChainAPI#createTransaction(me.benzo.db.blockchain
     * .Transaction)
     */
    @Override
    public Response createTransaction(final Transaction data) {
        this.blockchain.getCurrentTransactions().add(data);

        return Response.ok(this.blockchain.getLastBlock() != null ? this.blockchain.getLastBlock().getIndex() + 1 : 0)
                .build();
    }

}