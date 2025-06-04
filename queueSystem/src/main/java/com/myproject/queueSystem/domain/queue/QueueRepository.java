package com.myproject.queueSystem.domain.queue;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    @Query("SELECT q.code FROM Queue q WHERE q.type = :type ORDER BY q.id DESC LIMIT 1")
    String findLastCodeByType(@Param("type") TYPE type);

    @Query("SELECT q FROM Queue q WHERE q.status = 'PENDING' AND q.type = 'PREFERENCIAL' ORDER BY q.timestamp ASC LIMIT 1")
    Optional<Queue> findLastPreferencialQueue();

    @Query("SELECT q FROM Queue q WHERE q.status = 'PENDING' AND q.type = 'NORMAL' ORDER BY q.timestamp ASC LIMIT 1")
    Optional<Queue> findLastNormalQueue();

    @Modifying
    @Transactional
    @Query("UPDATE Queue q SET q.status = 'CANCELLED' WHERE q.code = :code ")
    void cancelQueueByCode(String code);

    @Query("SELECT q FROM Queue q WHERE q.status = :status And q.code IS NOT NULL")
    List<Queue> findAllByTypeAndStatus(STATUS status);

    @Modifying
    @Transactional
    @Query("UPDATE Queue q SET q.code = null")
    void clearAllCodes();

    @Query("SELECT q.code FROM Queue q ORDER BY q.id DESC LIMIT 1")
    String findLastCode();

    @Query("SELECT q FROM Queue q WHERE q.status = 'PENDING' ORDER BY q.timestamp ASC LIMIT 1")
    Optional<Queue> findLastQueue();
}
