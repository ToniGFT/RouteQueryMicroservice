package com.workshop.route.domain.repository;

import com.workshop.route.domain.model.aggregates.Route;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteQueryRepository extends ReactiveMongoRepository<Route, ObjectId> {
}
