package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRecordRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {

    private final UserRecordRepository userRecordRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(UserRecordRepository userRecordRepository,
                               TransactionRecordRepository transactionRecordRepository,
                               RestTemplate restTemplate) {
        this.userRecordRepository = userRecordRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas")
    public void listen(Transaction transaction) {
        if (transaction == null) {
            System.out.println("⚠️ Received null transaction");
            return;
        }

        UserRecord sender = userRecordRepository.findById(transaction.getSenderId()).orElse(null);
        UserRecord recipient = userRecordRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) {
            System.out.println("⚠️ Invalid sender or recipient");
            return;
        }

        if (sender.getBalance() < transaction.getAmount()) {
            System.out.println("⚠️ Sender has insufficient balance");
            return;
        }

        float preBalance = sender.getBalance();

        // 🔥 Pide incentivo
        Incentive incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive", transaction, Incentive.class
        );

        float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0.0f;

        // Actualiza balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

        userRecordRepository.save(sender);
        userRecordRepository.save(recipient);

        // Guarda la transacción
        TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), preBalance, incentiveAmount);
        transactionRecordRepository.save(record);

        System.out.println("✅ Transaction processed: " + transaction.getAmount() + ", Incentive: " + incentiveAmount);
    }

    // 🔥 Método para imprimir el balance de Wilbur
    public void imprimirBalanceDeWilbur() {
        UserRecord wilbur = userRecordRepository.findByUsername("wilbur");
        if (wilbur != null) {
            System.out.println("Balance final de Wilbur: " + (int) wilbur.getBalance());
        } else {
            System.out.println("No se encontró a Wilbur 😢");
        }
    }
}






