package com.example.opcuaserver;

import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.EventItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedAddressSpaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class CustomNamespace extends ManagedAddressSpaceWithLifecycle {
    public CustomNamespace(OpcUaServer server) {
        super(server);
    }

    public CustomNamespace(OpcUaServer server, UaNodeManager nodeManager) {
        super(server, nodeManager);
    }

    @Override
    public void onCreateDataItem(ReadValueId itemToMonitor, Double requestedSamplingInterval, UInteger requestedQueueSize, BiConsumer<Double, UInteger> revisionCallback) {
        super.onCreateDataItem(itemToMonitor, requestedSamplingInterval, requestedQueueSize, revisionCallback);
    }

    @Override
    public void onModifyDataItem(ReadValueId itemToModify, Double requestedSamplingInterval, UInteger requestedQueueSize, BiConsumer<Double, UInteger> revisionCallback) {
        super.onModifyDataItem(itemToModify, requestedSamplingInterval, requestedQueueSize, revisionCallback);
    }

    @Override
    public void onCreateEventItem(ReadValueId itemToMonitor, UInteger requestedQueueSize, Consumer<UInteger> revisionCallback) {
        super.onCreateEventItem(itemToMonitor, requestedQueueSize, revisionCallback);
    }

    @Override
    public void onModifyEventItem(ReadValueId itemToModify, UInteger requestedQueueSize, Consumer<UInteger> revisionCallback) {
        super.onModifyEventItem(itemToModify, requestedQueueSize, revisionCallback);
    }

    @Override
    public void onDataItemsCreated(List<DataItem> dataItems) {

    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {

    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {

    }

    @Override
    public void onEventItemsCreated(List<EventItem> eventItems) {
        super.onEventItemsCreated(eventItems);
    }

    @Override
    public void onEventItemsModified(List<EventItem> eventItems) {
        super.onEventItemsModified(eventItems);
    }

    @Override
    public void onEventItemsDeleted(List<EventItem> eventItems) {
        super.onEventItemsDeleted(eventItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {

    }
}
