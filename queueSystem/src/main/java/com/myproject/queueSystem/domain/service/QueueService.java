package com.myproject.queueSystem.domain.service;

import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.domain.queue.QueueRepository;
import com.myproject.queueSystem.domain.queue.STATUS;
import com.myproject.queueSystem.domain.queue.TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueueService {

    @Autowired
    QueueRepository queueRepository;

    public Queue generatedQueue(TYPE type) {
        // Instância
        Queue queue = new Queue();
        queue.setType(type);
        queue.setStatus(STATUS.PENDING);
        queue.setTimestamp(LocalDateTime.now());

        // Busca a ultima senha
        String lastCode = queueRepository.findLastCodeByType(type);

        // proxima senha
        int nextNumber = 1;
        if (lastCode != null && lastCode.length() > 1) { // verifica se há senha
            String numericPart = lastCode.substring(1);
            nextNumber = Integer.parseInt(numericPart) + 1;
        }

        // Gera código com prefixo e número formatado com 3 dígitos
        String prefix = type == TYPE.NORMAL ? "N" : "P";
        String code = prefix + String.format("%03d", nextNumber);

        queue.setCode(code);
        queueRepository.save(queue);
        return queue;
    }


    public Queue getNextQueue() {
        return null;
    }

    public void cancelQueue() {
    }

    public List<Queue> getQueuelist() {
        return null;
    }

    public void resetQueues() {
        return;
    }


}
