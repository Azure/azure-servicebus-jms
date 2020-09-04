package com.microsoft.azure.servicebus.jms;

/**
* <li>If REPLACE is configured then all reconnect URIs other than the one for the current server are replaced with those provided by the remote peer.</li>
* <li>If ADD is configured then the URIs provided by the remote are added to the existing set of reconnect URIs, with de-duplication.</li>
* <li>If IGNORE is configured then any updates from the remote are dropped and no changes are made to the set of reconnect URIs in use.</li>
*/
public enum ReconnectAmqpOpenServerListAction {
    REPLACE, ADD, IGNORE;
}
