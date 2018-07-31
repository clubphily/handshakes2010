package org.marginal.brigit.bluetooth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.marginal.brigit.pngplayer.PNGPlayerCanvas;
import org.marginal.brigit.util.BluetoothUtil;
import org.marginal.brigit.util.VectorUtil;

public class Server implements Runnable {

    private LocalDevice local = null;
    private StreamConnectionNotifier server = null;
    private String connectionURL;
    private Thread serverThread = null;
    private Streamer streamer;
    private Vector myImages;

    public Server(){

    }

    public void start(final Vector myImages) {
        this.myImages = myImages;
        streamer = new Streamer();
        serverThread = new Thread(this);
        serverThread.start();
    }

    public void run() {
        if (createService()) {
            streamer.start();
        } else {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException ioe){}
            }
        }
    }

    public void stop() {
        if(streamer != null)
            streamer.stop();
            streamer = null;
        if (serverThread != null) {
            /*serverThread.interrupt();
            serverThread = null;*/
            try {
                serverThread.join();
            } catch (InterruptedException e) {
                /*Shit happens*/
            }
        }
    }

    private boolean createService() {
        ServiceRecord record = null;
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);
        } catch (BluetoothStateException e) {
            e.printStackTrace();
            return false;
        }

        connectionURL = "btspp://localhost:"+BluetoothUtil.UUID_STRING+"+;name=BTBench";

        try {
            server = (StreamConnectionNotifier) Connector.open(connectionURL);
        } catch (IOException e1) {
            return false;
        }

        try {
            record = local.getRecord(server);
        } catch (IllegalArgumentException iae) {
            return false;

        }

        DataElement elm = null;

        /*
         * Set public browse root in browsegrouplist, making service
         * public browseable
         */
        elm = new DataElement(DataElement.DATSEQ);
        elm.addElement(new DataElement(DataElement.UUID, new UUID(0x1002)));
        record.setAttributeValue(0x0005, elm);

        /* Set service description */
        elm = new DataElement(DataElement.STRING, "Get your *** images, you dirty little cupcake.");
        record.setAttributeValue(0x101, elm);

        /* Set service provider name */
        elm = new DataElement(DataElement.STRING, "Brigitte, Johnny und Philipp");
        record.setAttributeValue(0x102, elm);

        /* Update the record, else changes are lost */
        try {
            local.updateRecord(record);
        } catch (ServiceRegistrationException e3) {
            return false;
        }
        return true;
    }

    private class Streamer implements Runnable {

        private Thread streamerThread;
        private StreamConnection conn = null;
        private DataInputStream in = null;
        private DataOutputStream out = null;

        public void start() {
            streamerThread = new Thread(this);
            streamerThread.start();
        }

        public void run() {
            if (server == null) {
                return;
            }
            try {
                conn = server.acceptAndOpen();
                PNGPlayerCanvas.startSharingImage();
            } catch (ServiceRegistrationException sre) {
                cleanup();
                return;
            } catch (IOException e2) {
                cleanup();
                return;
            }
            try {
                in = conn.openDataInputStream();
                out = conn.openDataOutputStream();
            } catch (IOException e4) {
                cleanup();
                streamerThread = null;
                return;
            }

            String otherImagesVec = "";

            try {
                otherImagesVec = in.readUTF();
            } catch (EOFException eof) {
                cleanup();
                return;
            } catch (IOException e5) {
                cleanup();
                return;
            }
            int offer = myOffer(otherImagesVec);
            /* Get the time and do actual communication */
            try {
                out.writeInt(offer);
                out.flush();
            } catch (IOException ioe) {
                cleanup();
                return;
            }
            cleanup();
        }

        public void stop() {
            cleanup();
            if (streamerThread != null) {
                try {
                    streamerThread.join();
                } catch (InterruptedException e) {
                }
            }
        }

        private int myOffer (String otherImagesVec) {
            Vector otherImages = VectorUtil.stringToIntVector(otherImagesVec);
            Vector offers = VectorUtil.difference(myImages, otherImages);
            int vecSize = offers.size(), result = -1;
            Random random = new Random();
            if (offers != null && vecSize > 0) {
                int randPosition = 0;
                if(vecSize > 1) {
                    randPosition = random.nextInt(vecSize - 1);
                }
                result = ((Integer)offers.elementAt(randPosition)).intValue();
            }
            return result;
        }

        /* Close streams and notifier */
        private void cleanup() {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (server != null) {
                    server.close();
                }
            } catch (IOException ioe) {
                //Shit happens. All the time.
            }
        }
    }
}
