package io.github.debop.springdata.jpa.mapping.onetoone

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne


interface HusbandRepository : JpaRepository<Husband, Long>

interface WifeRepository : JpaRepository<Wife, Long>

/*
    create table onetoone_husband (
       id bigint generated by default as identity,
        name varchar(255),
        wife_id bigint,
        primary key (id)
    )

    create table onetoone_wife (
       id bigint generated by default as identity,
        name varchar(255),
        primary key (id)
    )

    alter table onetoone_husband
       add constraint FKp3x9l5r3av7vwj2viod9mn69j
       foreign key (wife_id)
       references onetoone_wife
 */

@Entity(name = "onetoone_husband")
data class Husband(var name: String) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null

    // Bidirectional one-to-one
    // husband table 에 wife_id 가 저장된다
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "wife_id")
    var wife: Wife? = null
}

@Entity(name = "onetoone_wife")
data class Wife(var name: String) {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null

    // Bidirectional one-to-one
    @OneToOne(mappedBy = "wife", fetch = LAZY)
    var husband: Husband? = null
}