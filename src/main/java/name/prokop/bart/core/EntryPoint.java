package name.prokop.bart.core;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import reactor.core.publisher.Flux;

/**
 *
 * @author Bart Prokop
 */
public class EntryPoint {

    public static void main(String... args) {
        Flux<HttpServerExchange> requestPublisher = Flux.create(emitter -> {
            Undertow.Builder undertowBuilder = Undertow.builder();
            undertowBuilder.addHttpListener(8080, "localhost");
            undertowBuilder.setHandler(new HttpHandler() {
                @Override
                public void handleRequest(final HttpServerExchange exchange) throws Exception {
                    if (exchange.isInIoThread()) {
                        exchange.dispatch(this);
                        return;
                    }
                    emitter.next(exchange);
                }
            });
            Undertow server = undertowBuilder.build();
            server.start();
        });

        requestPublisher.subscribe(exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Hello World");
        });
    }

}
