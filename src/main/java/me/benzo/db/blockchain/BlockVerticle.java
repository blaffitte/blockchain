/**
 * 
 */
package me.benzo.db.blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.exceptions.Exceptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import me.benzo.db.blockchain.model.Block;
import me.benzo.db.blockchain.model.NewBlock;

/**
 * @author blaffitte
 *
 */
public class BlockVerticle extends AbstractVerticle {
    
    protected EventBus blockChainBus;
    protected List<Block> blockChain = new LinkedList<>();

    @Override
    public void start() throws Exception {
       blockChainBus = vertx.eventBus();
       blockChainBus.consumer("CreateBlock", this::createNewBlock);
       blockChainBus.consumer("ForgeBlock", this::miningBlock);
    }
    
    protected void miningBlock(Message<Block> message) {
        Block lastBlock = message.body();
        int proof = this.createProofOfWork(lastBlock.getProof(), lastBlock.getPreviousHash());
        
        this.blockChain.add(lastBlock);
        NewBlock nb = new NewBlock();
        nb.setProof(proof);
        blockChainBus.publish("CreateBlock", nb);
    }
    
    protected void createNewBlock(Message<NewBlock> message) {
        NewBlock nb = message.body();
        Block block = new Block();
        block.setIndex(this.blockChain.size());
        block.setTimestamp(new Timestamp(new Date().getTime()));
        block.setContents(new LinkedList<>());
        block.setProof(nb.getProof());
        block.setPreviousHash(nb.getPreviousHash() != null ? nb.getPreviousHash() : signingBlock(this.blockChain.get(this.blockChain.size() - 1)));

        blockChainBus.publish("BlockCreated", block);
      
    }
    
    private int createProofOfWork(int lastProof, String previousHash) {
        int proof = 0;
        while (!isValidProof(lastProof, proof, previousHash)) {
            proof++;
        }
        return proof;
    }

    private boolean isValidProof(int lastProof, int proof, String previousHash) {
        String guess = String.format("%s%s%s", lastProof, proof, previousHash);
        String result = signing(guess);
        return result.startsWith("0000");
    }

    private String signingBlock(Block block) {
        String blockText = JsonObject.mapFrom(block).encode();
        return signing(blockText);
    }

    private String signing(String data)  {
        MessageDigest sha256 ;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
           throw Exceptions.propagate(e);
        }
        StringBuilder hashBuilder = new StringBuilder();

        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] hash = sha256.digest(bytes);

        for (byte x : hash) {
            hashBuilder.append(String.format("%02X", x));
        }

        return hashBuilder.toString();
    }
    
    public boolean resolveConflicts()  {
        // List<Block> newChain = null;
        // // int maxLength = this.chain.size();
        //
        // for (Node node : this.nodes) {
        // URL url = new URL(node.getAddress(), "/chain");
        // GetRequest request = Unirest.get(url.toString());
        //
        // HttpResponse<JsonNode> response = request.asJson();
        //
        // if (response.getStatus() == 200) {
        // JSONObject json = response.getBody().getObject();
        // JSONArray jsonChain = json.getJSONArray("chain");
        // List<Block> blocks = Block.fromJsonArray(jsonChain);
        //
        // if (blocks.size() > this.chain.size() && isValidChain(blocks)) {
        // // maxLength = blocks.size();
        // newChain = blocks;
        // }
        // }
        // }
        //
        // if (newChain != null) {
        // this.chain = newChain;
        // return true;
        // }

        return false;
    }
}
