/**
 *
 */
package me.benzo.db.blockchain;

import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

/**
 * @author blaffitte
 *
 */
public class RestApiVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        EventBus bus = vertx.eventBus();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route(HttpMethod.GET, "/api/chain").handler(routingContext -> {
            // NewBlock Body
            bus.consumer("GettingBlockChainResponse", message -> {
                routingContext.response().setStatusCode(200).end(message.body().toString());
            });
            bus.publish("GettingBlockChain", "");
            routingContext.response().setStatusCode(200).end();
        });
        router.route(HttpMethod.POST, "/api/chain/block").handler(routingContext -> {
            // NewBlock Body
            bus.publish("CreateBlock", routingContext.getBodyAsJson());
            routingContext.response().setStatusCode(200).end();
        });

        router.route(HttpMethod.POST, "/api/chain/mine").handler(routingContext -> {
            // NewBlock Body
            bus.publish("ForgeBlock", routingContext.getBodyAsJson());
            routingContext.response().setStatusCode(200).end();
        });
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

}
