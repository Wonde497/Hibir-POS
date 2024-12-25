// IComm.aidl
package com.xcheng.wiredecr;

// Declare any non-default types here with import statements

interface IComm {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    int getConnectTimeout();
    void setConnectTimeout(int var1);
    int getSendTimeout();
    void setSendTimeout(int var1);
    int getRecvTimeout();
    void setRecvTimeout(int var1);
    void connect(String deviceName);
	int getConnectStatus();
    void disconnect();
    void send(in byte[] var1);
    byte[] recv(int var1);
    byte[] recvNonBlocking();
    void reset();
    void cancelRecv();
    void open();
    void close();
}
