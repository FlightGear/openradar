/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2012-2016,2018 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.multiplayer;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;

public abstract class MultiplayerClient<T extends Player> extends AbstractMultiplayerEndpoint<T> {
    protected static Logger log = LogManager.getLogger("de.knewcleus.fgfs.multiplayer");
    private final static long APPLICATION_START_TIME_MILLIS = System.currentTimeMillis();
    protected final InetAddress serverAddress;
    protected final int serverPort;
    protected final int localPort;
    protected final List<OutgoingMessage> chatQueue = Collections.synchronizedList(new ArrayList<OutgoingMessage>());
    protected volatile OutgoingMessage activeChatMessage = null;
    protected String frequency = "000.00";
    protected int radarRange = 100;
    protected final boolean packetForward1;
    protected final InetAddress packetForwardHost1;
    protected final int packetForwardPort1;
    protected final boolean packetForward2;
    protected final InetAddress packetForwardHost2;
    protected final int packetForwardPort2;
	/** Version of MP protocol null: old client, 1: compatible mode, 2: 2017.2 */
	protected Integer protocolVersion=null;

    
    protected volatile long lastPositionUpdateTimeMillis;

    public MultiplayerClient(IPlayerRegistry<T> playerRegistry, String mpServer, int mpServerPort, int mpLocalPort,
                             boolean packetForward1, String packetForwardHost1, int packetForwardPort1,
                             boolean packetForward2, String packetForwardHost2, int packetForwardPort2) throws IOException {
        super(playerRegistry, mpLocalPort);
        this.serverAddress = InetAddress.getByName(mpServer);
        this.serverPort = mpServerPort;
        this.localPort = mpLocalPort;
        this.packetForward1 = packetForward1;
        this.packetForwardHost1 = InetAddress.getByName(packetForwardHost1);
        this.packetForwardPort1 = packetForwardPort1;
        this.packetForward2 = packetForward2;
        this.packetForwardHost2 = InetAddress.getByName(packetForwardHost2);
        this.packetForwardPort2 = packetForwardPort2;
        lastPositionUpdateTimeMillis = System.currentTimeMillis();
    }

    protected void startSending() {
        Thread chatPositionSender = new Thread("OpenRadar - messageSender") {
            @Override
            public void run() {
                int counter = 0;
                while (true) {
                    try {
//                        synchronized (this) { // does lock the sending thread. This has no influence
                            // send every second if queue is empty, otherwise
                            // every 250 ms.
                            if (!chatQueue.isEmpty() || activeChatMessage != null || counter > 1) {
                                sendPositionUpdate();
                                counter = 0;
                            } else {
                                counter++;
                            }
//                        }
                        sleep(250);
                    } catch (InterruptedException e) {
                    	log.warn("OpenRadar - messageSender was interrupted, Cause for a former error...");
                    }
                }
            }
        };
        chatPositionSender.setDaemon(true);
        chatPositionSender.start();
    }

    // public void sendPacket(MultiplayerPacket mppacket) throws
    // MultiplayerException {
    // sendPacket(serverAddress, serverPort, mppacket);
    // }

    // @Override
    // protected void update() {
    // super.update();
    // // sendPositionUpdate(); moved to thread chatPositionSender to remove
    // link between sending and reception
    // }

    protected synchronized void sendPositionUpdate() {
        if(getCallsign()==null) return;

        PositionMessage positionMessage = new PositionMessage();
        positionMessage.setTime((double) (System.currentTimeMillis() - APPLICATION_START_TIME_MILLIS) / 1000d);
        positionMessage.setLag(0.25d);
        positionMessage.setPosition(getPosition());
        positionMessage.setOrientation(getOrientation());
        positionMessage.setLinearVelocity(getLinearVelocity());
        positionMessage.setModel(getModel());
        
        if (activeChatMessage != null) {
            if (System.currentTimeMillis() - activeChatMessage.firstSendTime > 3000 && activeChatMessage.sendCounter > 9) {
                // stop sending after 3 seconds
                activeChatMessage = null;
            }
        }
        if (activeChatMessage == null && chatQueue.size() > 0) {
            // there is a chat message waiting to be sent
            activeChatMessage = chatQueue.remove(0);
            activeChatMessage.firstSendTime = System.currentTimeMillis();
        }
        positionMessage.putProperty("sim/multiplay/transmission-freq-hz", this.frequency);
        // send it
        if (activeChatMessage != null) {
            positionMessage.putProperty("sim/multiplay/chat", activeChatMessage.message);
            activeChatMessage.sendCounter++;
            // System.out.println("Chat message sent: Counter "+activeChatMessage.sendCounter+", ms since first transmission: "+(System.currentTimeMillis()-activeChatMessage.firstSendTime));
        } else {
            positionMessage.putProperty("sim/multiplay/chat", "");
        }
        protocolVersion = (protocolVersion!=null && protocolVersion<2) ? 2 : 1;
        positionMessage.putProperty("sim/multiplay/protocol-version", protocolVersion);
        
        // if(chatQueue.size()>2) {
        // System.out.println("WARNING: Chat outgoing queue size: "+chatQueue.size());
        // }
        MultiplayerPacket mppacket = new MultiplayerPacket(getCallsign(), getRadarRange(), positionMessage);
        try {
            sendPacket(serverAddress, serverPort, mppacket);
            // System.out.println("Message sent");
        } catch (MultiplayerException e) {
            log.error("Error in FGFS networking!",e);
        }
        lastPositionUpdateTimeMillis = System.currentTimeMillis();
    }

    /**
     * Adds the message to the outgoing queue...
     *
     * @param message
     */
    public synchronized void sendChatMessage(String f, String message) {
        // chatQueue is synchronized itself
        while(message!=null) {
            if(message.length()>128) {
                // message too long, send next line
                int sep = findSeparator(message);
                String nextLine = message.substring(0,sep);
                chatQueue.add(new OutgoingMessage(nextLine));
                message = message.substring(sep).trim();
            } else {
                // message is short enough / send the rest
                chatQueue.add(new OutgoingMessage(message.trim()));
                message=null; // end while loop
            }
        }
        setFrequency(f);
    }
    

    private int findSeparator(String message) {
        String line = message.substring(0,128);
        int sep = line.lastIndexOf(" ");
        if(sep<100) {
            // hard line break
            sep=128;
        }
        return sep;
    }

    public synchronized void setFrequency(String f) {
        if (!"".equals(f.trim())) {
            f = f.replaceAll(",", ".");
            BigDecimal bd = new BigDecimal(f).multiply(new BigDecimal(1000000));
            this.frequency = String.format("%1.0f", bd);
        } else {
            this.frequency = "100000000";
        }
    }

    public synchronized int getRadarRange() {
		return radarRange;
	}

	public synchronized void setRadarRange(int radarRange) {
		radarRange = radarRange<10 ? 10 : radarRange;
		radarRange = radarRange>2000 ? 1999 : radarRange; 
		this.radarRange = radarRange;
	}

	/**
     * RECEPTION: This method is called by the receiving thread. It does not
     * modify any local variables.
     */
    @Override
    protected void processPacket(T player, MultiplayerPacket mppacket) throws MultiplayerException {
        if (mppacket.getMessage() instanceof PositionMessage) {
            PositionMessage positionMessage = (PositionMessage) mppacket.getMessage();
            
            String chatMessage = positionMessage.getProperty("sim/multiplay/chat");
            // if(player.callsign.startsWith("TE")) {
            // System.out.println(player.callsign+": '" +chatMessage+"'");
            // }
            if (chatMessage != null && !chatMessage.isEmpty()) {
                String frequency = "";
                String f = (String) positionMessage.getProperty("sim/multiplay/transmission-freq-hz");
                if (f != null) {
                    BigDecimal bdFreq = new BigDecimal(f);
                    bdFreq = bdFreq.divide(new BigDecimal(1000000));
                    frequency = String.format("%1.3f", bdFreq);
                }

                notifyChatListeners(mppacket.getCallsign(), frequency, chatMessage);
            }
            // transponder data
            Integer transponderSquawkCode = (Integer) positionMessage.getProperty("instrumentation/transponder/id-code");
            player.setTranspSquawkCode(transponderSquawkCode);
            Integer transponderAltitude = (Integer) positionMessage.getProperty("instrumentation/transponder/altitude");
            player.setTranspAltitude(transponderAltitude);
//            System.out.println(transponderAltitude);
//            for(String key : positionMessage.getProperties().keySet()) {
//                if(key.contains("ident")) {
//                    System.out.println(positionMessage.getProperty(key));
//                }
//            }
            Boolean b = positionMessage.getProperty("instrumentation/transponder/ident");
            if(b!=null && b==true) {
                player.startTranspIdent();
            }
            if(positionMessage.getProperty("instrumentation/transponder/inputs/mode")!=null) {
                int transponderMode = (Integer) positionMessage.getProperty("instrumentation/transponder/inputs/mode");
                player.setTranspMode(transponderMode);
            }

            player.updatePosition(System.currentTimeMillis(), positionMessage);
        } else {
            // MP Server messages deprecated
            // ChatMessage chatMessage = (ChatMessage)mppacket.getMessage();
            // notifyChatListeners(mppacket.getCallsign(),
            // chatMessage.getMessage());
        }

        // send to view only client, if reception mirroring is enabled
        if(packetForward1) {
            sendPacket(packetForwardHost1, packetForwardPort1, mppacket);
        }
        if(packetForward2) {
            sendPacket(packetForwardHost2, packetForwardPort2, mppacket);
        }
    }

    public abstract String getModel();

    public abstract String getCallsign();

    public abstract Position getPosition();

    public abstract Position getOrientation();

    public abstract Position getLinearVelocity();

    public synchronized Integer getProtocolVersion() {
    	return protocolVersion;
    }

    private class OutgoingMessage {
        volatile long firstSendTime = 0l;
        String message;
        volatile int sendCounter;

        public OutgoingMessage(String message) {
            this.message = message;
        }
    }
}
