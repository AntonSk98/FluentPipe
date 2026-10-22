package de.ansk98.fluentpipe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(FluentPipeTestConfiguration.class)
class FluentPipeApplicationTests {

    @Test
    void contextLoads() {
    }

}
