package com.myproject.queueSystem.controller;

import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.domain.queue.dto.QueueDTO;
import com.myproject.queueSystem.domain.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("queue/")
public class QueueController {

    @Autowired
    QueueService queueService;


    @PostMapping
    public ResponseEntity<Queue> generatedQueue(@RequestBody QueueDTO data) {
        return ResponseEntity.ok(queueService.generatedQueue(data.type()));
    }

    @PostMapping("/call")
    public ResponseEntity<Queue> callQueue() {
        Queue queue = queueService.getNextQueue();

        return ResponseEntity.ok(queue);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Boolean> cancelQueue() {
        queueService.cancelQueue();

        return ResponseEntity.ok(true);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Queue>> tolistQueues() {
        List<Queue> queues = queueService.getQueuelist();

        return ResponseEntity.ok(queues);
    }

    @PostMapping("/reset")
    public ResponseEntity<Boolean> toresetQueues() {
        queueService.resetQueues();

        return ResponseEntity.ok(true);
    }
}
