package com.myproject.queueSystem.domain.service;

import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.domain.queue.QueueRepository;
import com.myproject.queueSystem.domain.queue.TYPE;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QueueService {

    @Autowired
    QueueRepository queueRepository;

    public Queue generatedQueue(TYPE type) {
        return null;
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
