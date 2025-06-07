package com.myproject.queueSystem.License;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenseRepository extends JpaRepository<License, Long> {

    List<License> findAllByUsedAtIsNull();

}
