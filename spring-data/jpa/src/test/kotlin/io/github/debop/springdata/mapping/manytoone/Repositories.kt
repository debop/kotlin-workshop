package io.github.debop.springdata.mapping.manytoone

import org.springframework.data.jpa.repository.JpaRepository

interface BreweryRepository : JpaRepository<Brewery, Long>

interface BeerRepository : JpaRepository<Beer, Long>

interface JugMeterRepository : JpaRepository<JugMeter, Long>

interface JugRepository : JpaRepository<Jug, Long>