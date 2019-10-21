package net.kwatts.android.NetScanner.service;

import net.kwatts.android.NetScanner.service.IRemoteServiceCallback;

/**
 * Example of defining an interface for calling on to a remote service
 * (running in another process).
 */
interface IRemoteService {
    /**
     * Often you want to allow a service to call back to its clients.
     * This shows how to do so, by registering a callback interface with
     * the service.
     */
    void registerCallback(IRemoteServiceCallback cb);
    
    /**
     * Remove a previously registered callback interface.
     */
    void unregisterCallback(IRemoteServiceCallback cb);
}
