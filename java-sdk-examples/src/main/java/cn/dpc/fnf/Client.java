package cn.dpc.fnf;

import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

public class Client {
    static Logger logger = LoggerFactory.getLogger(Client.class);

    public static RSocketClient buildClient() {
        return buildClient(TcpClientTransport.create(7001));
    }


    public static RSocketClient buildClient(ClientTransport clientTransport) {
        Mono<RSocket> rSocketMono = RSocketConnector.create()
                .setupPayload(DefaultPayload.create("test"))
                .acceptor(SocketAcceptor.forFireAndForget(payload -> {
                    logger.info("Received Processed message: {}", payload.getDataUtf8());
                    payload.release();
                    return Mono.empty();
                }))
                .connect(clientTransport);

        RSocketClient rSocketClient = RSocketClient.from(rSocketMono);

        return rSocketClient;
    }
}
