package cn.dpc.fnf;

import io.rsocket.core.RSocketClient;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

public class FireAndForgetTest {
    public static void main(String[] args) throws InterruptedException {
        Server.start();

        RSocketClient rSocketClient = Client.buildClient();

        rSocketClient.fireAndForget(Mono.just(DefaultPayload.create("hello")))
                .block();

        Thread.sleep(4000);
    }
}
