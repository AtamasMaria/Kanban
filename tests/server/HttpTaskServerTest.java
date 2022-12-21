package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import server.adapters.InstantAdapter;
import server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

public class HttpTaskServerTest {
    KVServer kvServer;
    HttpTaskServer taskServer;
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    @BeforeEach
    public void start() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskServer = new HttpTaskServer();

    }

}

