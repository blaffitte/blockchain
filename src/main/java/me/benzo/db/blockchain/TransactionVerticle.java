/**
 *
 */
package me.benzo.db.blockchain;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import me.benzo.db.blockchain.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author blaffitte
 *
 */
public class TransactionVerticle extends AbstractVerticle {

    private EventBus blockChainBus;
    private List<Transaction> currentTransactions;

    @Override
    public void start() throws Exception {
        blockChainBus = vertx.eventBus();
        currentTransactions = new ArrayList<>();
        blockChainBus.consumer("transaction", this::consumeTransaction);
    }

    private void consumeTransaction(Message<JsonObject> message) {
        Transaction t = message.body().mapTo(Transaction.class);
        this.currentTransactions.add(t);
    }

}
