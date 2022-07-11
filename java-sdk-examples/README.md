# Rsocket java sdk examples

## 1. fire and forget

### Create Server

#### Create Accepter

```java
class MySocketAcceptor implements SocketAcceptor {
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
```

#### Create Transport
* TcpServerTransport
```java
TcpServerTransport.create(7000);
```

* WebsocketServerTransport
```java
 WebsocketServerTransport.create(7001);
```

#### StartServer
```java
 RSocketServer.create(socketAcceptor)
        .bind(transport)
        .block();
```

### Create RsocketClient

```java
        Mono<RSocket> rSocketMono = RSocketConnector.create()
                .setupPayload(DefaultPayload.create("test"))
                .acceptor(SocketAcceptor.forFireAndForget(payload -> {
                    logger.info("Received Processed message: {}", payload.getDataUtf8());
                    payload.release();
                    return Mono.empty();
                }))
                .connect(TcpClientTransport.create(9001));

        RSocketClient rSocketClient = RSocketClient.from(rSocketMono);
```

### Request With RSocketClient
```java
rSocketClient.fireAndForget(Mono.just(DefaultPayload.create("hello")))
        .block();
```


