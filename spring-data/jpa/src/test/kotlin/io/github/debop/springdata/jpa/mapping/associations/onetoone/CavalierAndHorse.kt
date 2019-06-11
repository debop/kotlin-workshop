package io.github.debop.springdata.jpa.mapping.associations.onetoone

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.CascadeType.ALL
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

interface CavalierRepository : JpaRepository<Cavalier, Long>

interface HorseRepository : JpaRepository<Horse, Long>

/*
    create table onetoone_cavalier (
       id bigint generated by default as identity,
        name varchar(255),
        horse_id bigint,
        primary key (id)
    )

    create table onetoone_horse (
       id bigint generated by default as identity,
        name varchar(255),
        primary key (id)
    )

    alter table onetoone_cavalier
       add constraint FK6ve3gxndy93uxv7vw4m010n9a
       foreign key (horse_id)
       references onetoone_horse
 */

@Entity(name = "onetoone_cavalier")
data class Cavalier(
    var name: String,

    @OneToOne(cascade = [ALL])
    @JoinColumn(name = "horse_id")
    var horse: Horse? = null
) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null
}

@Entity(name = "onetoone_horse")
data class Horse(var name: String) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null
}