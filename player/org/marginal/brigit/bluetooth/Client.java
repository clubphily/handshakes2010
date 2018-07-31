package org.marginal.brigit.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import org.marginal.brigit.pngplayer.IImageExchange;
import org.marginal.brigit.pngplayer.PNGPlayerCanvas;
import org.marginal.brigit.util.BluetoothUtil;
import org.marginal.brigit.util.VectorUtil;

public class Client implements Runnable {

    private LocalDevice local = null;
    private DiscoveryAgent agent = null;
    private Thread clientThread;
    private volatile Vector myImage;
    private String conURL;
    private StreamConnection conn = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;
    private volatile boolean running = false;

    public synchronized boolean isRunning(){
        return running;
    }

    public void start(final Vector myImages) {
        this.myImages = myImages;
        running = true;
        clientThread = new Thread(this);
        clientThread.start();
    }

    public void stop() {
        if (clientThread != null) {
            try {
                clientThread.join();
            } catch (InterruptedException e) {
                /*Shit happens*/
            }
        }
    }

    public void run() {
        try {
            local = LocalDevice.getLocalDevice();
        } catch (BluetoothStateException bse) {
            cleanup();
            return;
        }
        agent = local.getDiscoveryAgent();
        final UUID uuid = new UUID(BluetoothUtil.UUID_STRING, false);
        try {
            conURL = agent.selectService(uuid,
                    ServiceRecord.NOAUTHENTICATE_NOENCRYPT,
                    false);
        } catch (BluetoothStateException bse) {
            cleanup();
        }
        if (conURL == null || conURL.length() == 0) {
            cleanup();
            return;
        }

        try {
            conn = (StreamConnection) Connector.open(conURL);
            PNGPlayerCanvas.startReceivingImages();
        } catch (IOException e) {
            cleanup();
            return;
        }

        try {
            out = conn.openDataOutputStream();
            in = conn.openDataInputStream();
        } catch (IOException e1) {
            cleanup();
            return;
        }
        String myImageVec = VectorUtil.vectorString(myImage);
        try {
            out.writeUTF(myImageVec);
            out.flush();
        } catch (IOException e2) {
            cleanup();
            return;
        }
        int newImage = -1;
        try {
            newImage = in.readInt();
        } catch (IOException e3) {
            cleanup();
            return;
        }
        cleanup(newImage);
    }

    private void cleanup() {
        this.cleanup(-1);
    }

    private void cleanup(final int myNewImage) {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (conn != null) {
                conn.close();
            }
            PNGPlayerCanvas.boughtNewImage(myNewImage);
        } catch (IOException e) {
            // Shit happens. All the time.
        } finally {
            conURL = null;
            running = false;
        }
    }
}
