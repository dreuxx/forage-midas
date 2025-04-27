package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRecordRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskThreeTests {

    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRecordRepository userRecordRepository; // ✅ Agregamos esto

    @Test
    void task_three_verifier() throws InterruptedException {
        userPopulator.populate(); // Cargar usuarios
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");

        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine); // Mandar cada transacción
        }

        Thread.sleep(3000); // Esperar 3 segundos para procesar Kafka

        // 🧠 Buscar a Waldorf
        UserRecord waldorf = userRecordRepository.findByUsername("waldorf");

        if (waldorf != null) {
            System.out.println("👉 Balance final de Waldorf: " + waldorf.getBalance());
        } else {
            System.out.println("❌ No se encontró el usuario 'waldorf'");
        }
    }
}

