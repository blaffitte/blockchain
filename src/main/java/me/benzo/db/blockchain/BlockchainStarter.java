package me.benzo.db.blockchain;

import io.vertx.reactivex.core.Vertx;

public class BlockchainStarter {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
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
