package com.myproject.queueSystem.controller;

import com.myproject.queueSystem.License.LicenseService;
import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.domain.queue.STATUS;
import com.myproject.queueSystem.domain.queue.dto.QueueDTO;
import com.myproject.queueSystem.domain.service.QueueService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
@SecurityRequirement(name = "bearer-key")
public class QueueController {

    @Autowired
    QueueService queueService;



    @PostMapping("/call")
    public ResponseEntity<Queue> callQueue() {
        Queue queue = queueService.getNextQueue();

        return ResponseEntity.ok(queue);
    }

    @DeleteMapping("/cancel/{code}")
    public ResponseEntity<Boolean> cancelQueue(@PathVariable String code) {
        if (queueService.cancelQueue(code)){
            return ResponseEntity.ok(true);
        }

        return (ResponseEntity<Boolean>) ResponseEntity.notFound();
    }

    @GetMapping("/list")
    public ResponseEntity<List<Queue>> tolistQueues(
            @RequestParam(required = false) STATUS status) {
        List<Queue> queues = queueService.getQueuelist(status);

        return ResponseEntity.ok(queues);
    }

    @PutMapping("/reset")
    public ResponseEntity<Boolean> toresetQueues() {
        queueService.resetQueues();

        return ResponseEntity.ok(true);
    }
}
