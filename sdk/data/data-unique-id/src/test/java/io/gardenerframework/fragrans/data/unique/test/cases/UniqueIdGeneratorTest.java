package io.gardenerframework.fragrans.data.unique.test.cases;

import io.gardenerframework.fragrans.data.unique.UniqueIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class UniqueIdGeneratorTest {
    @Autowired
    private UniqueIdGenerator uniqueIdGenerator;

    @Test
    public void testHostId() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new UniqueIdGenerator(" ")
        );
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new UniqueIdGenerator("")
        );
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new UniqueIdGenerator("1234567")
        );
        Assertions.assertEquals("123456", new UniqueIdGenerator("123456").getNodeId());
        Assertions.assertEquals("000123", new UniqueIdGenerator("123").getNodeId());
    }

    @Test
    public void testGenerateId() throws InterruptedException {
        List<Thread> threads = new ArrayList<>(100);
        for (int i = 0; i < 3000; i++) {
            Thread thread = new Thread(
                    () -> {
                        for (int j = 0; j < 1000; j++) {
                            uniqueIdGenerator.nextId('T');
                        }
                    }
            );
            thread.start();
            threads.add(thread);
        }
        for (int i = 0; i < 1000; i++) {
            threads.get(i).join();
        }
    }
}