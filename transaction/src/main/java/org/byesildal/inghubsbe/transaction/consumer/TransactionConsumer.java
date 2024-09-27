package org.byesildal.inghubsbe.transaction.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.byesildal.inghubsbe.data.model.DepositEvent;
import org.byesildal.inghubsbe.data.model.WithdrawEvent;
import org.byesildal.inghubsbe.transaction.model.OrderEvent;
import org.byesildal.inghubsbe.transaction.service.TransactionMService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionConsumer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TransactionMService transactionMService;

    @RabbitListener(queues = "transaction")
    public void consume(String event) throws JsonProcessingException {

        // Event tipleri ->deposit, withdrawal, buy, sell, cancel
        // bunlara göre switch case ile service metotları olacak ve buradan yapılacak

        Map<String, Object> eventMap = mapper.readValue(event, Map.class);
        if (eventMap.containsKey("type")) {
            var type = eventMap.get("type");
            if (type.equals("deposit")) {
                var depositEvent = mapper.readValue(event, DepositEvent.class);
                transactionMService.deposit(depositEvent);
            }
            if (type.equals("withdrawal")) {
                var withdrawEvent = mapper.readValue(event, WithdrawEvent.class);
                transactionMService.withdraw(withdrawEvent);
            }
        }
        log.info(event);
    }
}
