/**
 * 
 */
package me.benzo.db.blockchain;

import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

/**
 * @author blaffitte
 *
 */
public class RestApiVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route(HttpMethod.GET, "/api/chain").handler(request -> {
            request.response().setStatusCode(200).end();
        });
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

}
