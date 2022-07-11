package cn.dpc.fnf;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class Server {
    public static void start() {
        start(new MySocketAcceptor(), TcpServerTransport.create(7001));
    }

    public static void start(SocketAcceptor socketAcceptor, ServerTransport transport) {
        RSocketServer.create(socketAcceptor)
                .bind(transport)
                .block();
    }

    static class MySocketAcceptor implements SocketAcceptor {
        Logger logger = LoggerFactory.getLogger(MySocketAcceptor.class);

        @Override
        public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
            this.logger.info("Receive a client connected with id: {}", setup.getDataUtf8());
            return Mono.just(new RSocketHandler(sendingSocket, setup.getDataUtf8()));
        }

        private static class RSocketHandler implements RSocket {
            Logger logger = LoggerFactory.getLogger(RSocketHandler.class);

            private final RSocket rSocket;
            private final String id;

            private RSocketHandler(RSocket rSocket, String id) {
                this.rSocket = rSocket;
                this.id = id;
            }

            @Override
            public Mono<Void> fireAndForget(Payload payload) {
                this.logger.info("Received a message [{}] from Client.ID[{}]", payload.getDataUtf8(), id);
                payload.release();

                return rSocket.fireAndForget(DefaultPayload.create("Processed " + payload.getDataUtf8()));
            }
        }
    }
}
