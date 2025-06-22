package com.myproject.queueSystem.queue;

import com.myproject.queueSystem.domain.queue.Queue;
import com.myproject.queueSystem.domain.queue.QueueRepository;
import com.myproject.queueSystem.domain.queue.STATUS;
import com.myproject.queueSystem.domain.queue.TYPE;
import com.myproject.queueSystem.domain.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QueueServiceTest {

    private QueueRepository queueRepository;
    private QueueService queueService;

    @BeforeEach
    void setup() {
        queueRepository = mock(QueueRepository.class);
        queueService = new QueueService();
        queueService.queueRepository = queueRepository;
    }

    @Test
    void testGeneratedQueueWithTypeNormal() {
        when(queueRepository.findLastCodeByType(TYPE.NORMAL)).thenReturn("N001");

        Queue queue = queueService.generatedQueue(TYPE.NORMAL);

        assertEquals(TYPE.NORMAL, queue.getType());
        assertEquals("N002", queue.getCode());
        assertEquals(STATUS.PENDING, queue.getStatus());

        verify(queueRepository).save(queue);
    }

    @Test
    void testGeneratedQueueWithTypePreferencial() {
        when(queueRepository.findLastCodeByType(TYPE.PREFERENCIAL)).thenReturn("P010");

        Queue queue = queueService.generatedQueue(TYPE.PREFERENCIAL);

        assertEquals(TYPE.PREFERENCIAL, queue.getType());
        assertEquals("P011", queue.getCode());
        assertEquals(STATUS.PENDING, queue.getStatus());

        verify(queueRepository).save(queue);
    }

    @Test
    void testGeneratedQueueWithoutType() {
        when(queueRepository.findLastCode()).thenReturn("0009");

        Queue queue = queueService.generatedQueue(null);

        assertNull(queue.getType());
        assertEquals("0010", queue.getCode());
        assertEquals(STATUS.PENDING, queue.getStatus());

        verify(queueRepository).save(queue);
    }

    @Test
    void testGetNextQueuePreferencial() {
        Queue preferencialQueue = new Queue();
        preferencialQueue.setStatus(STATUS.PENDING);
        preferencialQueue.setType(TYPE.PREFERENCIAL);

        when(queueRepository.findLastPreferencialQueue()).thenReturn(Optional.of(preferencialQueue));

        Queue result = queueService.getNextQueue();

        assertEquals(STATUS.CALLED, result.getStatus());
        verify(queueRepository).save(preferencialQueue);
    }

    @Test
    void testGetNextQueueNormalWhenNoPreferencial() {
        when(queueRepository.findLastPreferencialQueue()).thenReturn(Optional.empty());

        Queue normalQueue = new Queue();
        normalQueue.setStatus(STATUS.PENDING);
        normalQueue.setType(TYPE.NORMAL);

        when(queueRepository.findLastNormalQueue()).thenReturn(Optional.of(normalQueue));

        Queue result = queueService.getNextQueue();

        assertEquals(STATUS.CALLED, result.getStatus());
        verify(queueRepository).save(normalQueue);
    }

    @Test
    void testGetNextQueueLastQueueWhenNoPreferencialOrNormal() {
        when(queueRepository.findLastPreferencialQueue()).thenReturn(Optional.empty());
        when(queueRepository.findLastNormalQueue()).thenReturn(Optional.empty());

        Queue lastQueue = new Queue();
        lastQueue.setStatus(STATUS.PENDING);
        lastQueue.setType(TYPE.NORMAL);

        when(queueRepository.findLastQueue()).thenReturn(Optional.of(lastQueue));

        Queue result = queueService.getNextQueue();

        assertEquals(STATUS.CALLED, result.getStatus());
        verify(queueRepository).save(lastQueue);
    }

    @Test
    void testCancelQueue() {
        queueService.cancelQueue("CODE123");
        verify(queueRepository).cancelQueueByCode("CODE123");
    }

    @Test
    void testGetQueuelist() {
        when(queueRepository.findAllByTypeAndStatus(STATUS.PENDING)).thenReturn(List.of(new Queue()));
        List<Queue> list = queueService.getQueuelist(STATUS.PENDING);
        assertFalse(list.isEmpty());
    }

    @Test
    void testResetQueues() {
        queueService.resetQueues();
        verify(queueRepository).clearAllCodes();
    }
}
