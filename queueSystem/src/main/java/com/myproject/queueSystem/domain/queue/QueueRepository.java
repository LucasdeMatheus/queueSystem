package com.myproject.queueSystem.domain.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    @Query("SELECT q.code FROM Queue q WHERE q.type = :type ORDER BY q.id DESC LIMIT 1")
    String findLastCodeByType(@Param("type") TYPE type);
}
