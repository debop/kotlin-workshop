package io.github.debop.springdata.jpa.mapping.onetomany.set

import org.springframework.data.jpa.repository.JpaRepository

interface BiddingItemRepository : JpaRepository<BiddingItem, Long>

interface BidRepository : JpaRepository<Bid, Long>

interface ProductRepository : JpaRepository<Product, Long>
