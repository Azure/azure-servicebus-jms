package com.microsoft.azure.servicebus.jms;

/**
* If REPLACE is configured then all reconnect URIs other than the one for the current server are replaced with those provided by the remote peer.
* If ADD is configured then the URIs provided by the remote are added to the existing set of reconnect URIs, with de-duplication.
* If IGNORE is configured then any updates from the remote are dropped and no changes are made to the set of reconnect URIs in use.
*/
public enum ReconnectAmqpOpenServerListAction {
    REPLACE, ADD, IGNORE;
}
