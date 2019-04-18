// Copyright (c) 2019 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.javaapi.data;

import com.digitalasset.ledger.api.v1.TransactionOuterClass;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionTree {

    private final String transactionId;

    private final String commandId;

    private final String workflowId;

    private final Instant effectiveAt;

    private final Map<String, Event> eventsById;

    private final List<String> rootEventIds;

    private final String offset;

    public TransactionTree(String transactionId, String commandId, String workflowId, Instant effectiveAt, Map<String, Event> eventsById, List<String> rootEventIds, String offset) {
        this.transactionId = transactionId;
        this.commandId = commandId;
        this.workflowId = workflowId;
        this.effectiveAt = effectiveAt;
        this.eventsById = eventsById;
        this.rootEventIds = rootEventIds;
        this.offset = offset;
    }

    public static TransactionTree fromProto(TransactionOuterClass.TransactionTree tree) {
        String transactionId = tree.getTransactionId();
        String commandId = tree.getCommandId();
        String workflowId = tree.getWorkflowId();
        Instant effectiveAt = Instant.ofEpochSecond(tree.getEffectiveAt().getSeconds(), tree.getEffectiveAt().getNanos());
        Map<String, Event> eventsById = tree.getEventsByIdMap().values().stream().collect(Collectors.toMap(e -> {
            if (e.hasCreated()) return e.getCreated().getEventId();
            else if (e.hasExercised()) return e.getExercised().getEventId();
            else throw new IllegalArgumentException("Event is neither created not exercied: " + e);
        }, Event::fromProtoTreeEvent));
        List<String> rootEventIds = tree.getRootEventIdsList();
        String offset = tree.getOffset();
        return new TransactionTree(transactionId, commandId, workflowId, effectiveAt, eventsById, rootEventIds, offset);
    }

    public TransactionOuterClass.TransactionTree toProto() {
        return TransactionOuterClass.TransactionTree.newBuilder()
                .setTransactionId(this.transactionId)
                .setCommandId(this.commandId)
                .setWorkflowId(this.workflowId)
                .setEffectiveAt(com.google.protobuf.Timestamp.newBuilder().setSeconds(this.effectiveAt.getEpochSecond()).setNanos(this.effectiveAt.getNano()).build())
                .putAllEventsById(this.eventsById.values().stream().collect(Collectors.toMap(Event::getEventId, Event::toProtoTreeEvent)))
                .addAllRootEventIds(this.rootEventIds)
                .setOffset(this.offset)
                .build();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCommandId() {
        return commandId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public Instant getEffectiveAt() {
        return effectiveAt;
    }

    public Map<String, Event> getEventsById() {
        return eventsById;
    }

    public List<String> getRootEventIds() {
        return rootEventIds;
    }

    public String getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "TransactionTree{" +
                "transactionId='" + transactionId + '\'' +
                ", commandId='" + commandId + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", effectiveAt=" + effectiveAt +
                ", eventsById=" + eventsById +
                ", rootEventIds=" + rootEventIds +
                ", offset='" + offset + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTree that = (TransactionTree) o;
        return Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(commandId, that.commandId) &&
                Objects.equals(workflowId, that.workflowId) &&
                Objects.equals(effectiveAt, that.effectiveAt) &&
                Objects.equals(eventsById, that.eventsById) &&
                Objects.equals(rootEventIds, that.rootEventIds) &&
                Objects.equals(offset, that.offset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, commandId, workflowId, effectiveAt, eventsById, rootEventIds, offset);
    }
}
