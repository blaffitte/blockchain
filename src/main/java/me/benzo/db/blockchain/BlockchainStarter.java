package me.benzo.db.blockchain;

import io.vertx.reactivex.core.AbstractVerticle;

// java -jar target/blockchain.jar run me.benzo.db.blockchain.BlockchainStarter
public class BlockchainStarter extends AbstractVerticle {

    @Override
    public void start() {
        System.out.println("Start Rest API");
        vertx.deployVerticle("me.benzo.db.blockchain.RestApiVerticle", res -> {
            if (res.succeeded()) {
                System.out.println("Start Blockchain");
                vertx.deployVerticle("me.benzo.db.blockchain.BlockVerticle", trans -> {
                    if (trans.succeeded()) {
                        System.out.println("OK");
                    }
                });
            } else if (res.failed()) {
                System.out.println("KO " + res.cause().getMessage());
                vertx.close();
            }
        });

    }

}
