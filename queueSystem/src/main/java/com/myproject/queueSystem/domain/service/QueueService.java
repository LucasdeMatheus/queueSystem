package com.myproject.queueSystem.domain.service;

import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.domain.queue.QueueRepository;
import com.myproject.queueSystem.domain.queue.STATUS;
import com.myproject.queueSystem.domain.queue.TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        // primeiro preferenciais
        Optional<Queue> preferencial = queueRepository.findLastPreferencialQueue();
        if (preferencial.isPresent()){
            preferencial.get().setStatus(STATUS.CALLED);
            queueRepository.save(preferencial.get());
            return preferencial.get();
        }

        // caso não tenha preferenciais
        Optional<Queue> normal = queueRepository.findLastNormalQueue();
        if (normal.isPresent()){
            normal.get().setStatus(STATUS.CALLED);
            queueRepository.save(normal.get());
            return normal.get();
        }

        // caso não tenha proxima senha
        return null;
    }

    public boolean cancelQueue(String code) {
        queueRepository.cancelQueueByCode(code);
        return true;
    }

    public List<Queue> getQueuelist(STATUS status) {

        return queueRepository.findAllByTypeAndStatus(status);
    }

    public void resetQueues() {
        queueRepository.clearAllCodes();
        return;
    }


}
