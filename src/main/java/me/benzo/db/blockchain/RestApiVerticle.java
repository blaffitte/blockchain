/**
 * 
 */
package me.benzo.db.blockchain;

import io.vertx.reactivex.core.AbstractVerticle;

/**
 * @author blaffitte
 *
 */
public class RestApiVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.createHttpServer().requestHandler(req -> { }).listen(8080);
    }

}
