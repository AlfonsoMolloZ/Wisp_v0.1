package com.wisp.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wisp.app.entity.Planes;

public interface PlanesRepository extends JpaRepository<Planes, Long> {

}
