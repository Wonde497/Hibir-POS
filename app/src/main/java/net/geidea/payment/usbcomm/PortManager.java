package net.geidea.payment.usbcomm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.xcheng.wiredecr.IComm;


/**
 * Created by guangyi.peng on 2017/3/1.
 */
public class PortManager {
    private final static String TAG = USBCommunicationActivity.Companion.getTAG();


    public interface PortManagerListener{
        public void onServiceConnected();
    }
    public PortManager(Activity activity, PortManagerListener listener){
        this.mActivity = activity;
        this.mListener = listener;
    }
    private Activity mActivity;
    private PortManagerListener mListener;

    private IComm mPortService;

    private ServiceConnection mConnectionService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPortService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPortService = IComm.Stub.asInterface(service);
            mListener.onServiceConnected();
        }
    };

    public void onPortStart(){
        Intent intent=new Intent();
        intent.setPackage("com.xcheng.wiredecr");
        intent.setAction("com.xcheng.wiredecr.IWireEcrService");
        mActivity.startService(intent);
        mActivity.bindService(intent, mConnectionService, Context.BIND_AUTO_CREATE);
    }
    public void onPrinterStop(){
        ///*
        try{
            mActivity.unbindService(mConnectionService);
        }catch(Exception e){

        }finally{
            mActivity.finish();
        }
        //*/
        //mActivity.finish();
    }

    public int getConnectTimeout(){
		int connTimeout = 0;
        try {
            connTimeout = mPortService.getConnectTimeout();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return connTimeout;
    }

	public void setConnectTimeout(int var1) {
		try {
            mPortService.setConnectTimeout(var1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}


	public int getSendTimeout() {
		int sendTimeout = 0;
		try {
            sendTimeout = mPortService.getSendTimeout();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return sendTimeout;
	}

	public void setSendTimeout(int var1) {
		try {
            mPortService.setSendTimeout(var1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	public int getRecvTimeout() {
		int recvTimeout = 0;
		try {
            recvTimeout = mPortService.getRecvTimeout();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return recvTimeout;
	}

	public void setRecvTimeout(int var1) {
		try {
            mPortService.setRecvTimeout(var1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	public void connect(String deviceName) {
		try {
            mPortService.connect(deviceName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}


	public void disconnect(){
		try {
            mPortService.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}


	public void send(byte[] var1) {
		try {
            mPortService.send(var1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}


	public byte[] recv(int var1) {
		byte[] recvData = null;
		try {
            recvData = mPortService.recv(var1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("qisai", "recv e =" + e);
            e.printStackTrace();
        }
		return recvData;
	}


	public byte[] recvNonBlocking() {
		byte[] recvNonBlockingData = null;
		try {
            recvNonBlockingData = mPortService.recvNonBlocking();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return recvNonBlockingData;
	}

	public void reset() {
		try {
            mPortService.reset();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

    public void open() {
        try {
            mPortService.open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            mPortService.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

	public void cancelRecv() {
		try {
            mPortService.cancelRecv();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

    public int getConnectStatus() {
		int connectStatus = 0;
		try {
            connectStatus = mPortService.getConnectStatus();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return connectStatus;    //0:disconnect  1:connecting   2:connected
	}
}
