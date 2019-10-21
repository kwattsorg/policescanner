package net.kwatts.android.NetScanner.service;

oneway interface IRemoteServiceCallback {
    /**
     * Called when the service has a new value for you.
     */
    void valueChanged(int value);
    void statusMessage(String statusMessage);
}
