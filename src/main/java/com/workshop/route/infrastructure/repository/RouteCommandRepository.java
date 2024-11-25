package com.workshop.route.infrastructure.repository;

import com.workshop.route.domain.model.aggregates.Route;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteCommandRepository extends ReactiveMongoRepository<Route, ObjectId> {
}
