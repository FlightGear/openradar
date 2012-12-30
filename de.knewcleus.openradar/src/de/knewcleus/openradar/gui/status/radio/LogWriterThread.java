package de.knewcleus.openradar.gui.status.radio;

import java.io.BufferedInputStream;

import de.knewcleus.openradar.gui.LogWindow;

public class LogWriterThread implements Runnable {

    private Radio radio = null;
    private LogWindow logWindow = null;
    private Process process = null;
    private String tabName = null;
    private volatile boolean isRunning = true;
    
    public LogWriterThread(LogWindow logWindow, Radio radio, Process process) {
        this.logWindow = logWindow;
        this.process = process;
        this.radio = radio;
        this.tabName=radio.getKey();
        Thread thread = new Thread(this, "OpenRadar - FGCom Log Writer");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
        
        try {
            while(isRunning) {
                int len = isRunning ? bis.read(buffer) : 0;
                synchronized(this) {
                    if(len>0 && isRunning) {
                        String content = new String(buffer,0,len);
                        // System.out.println(content);
                        if(content.contains("rejected") || content.contains("Hanging up")) {
                            radio.setConnectedToServer(false);
                        } else if(content.contains("accepted")){
                            radio.setConnectedToServer(true);
                        }
                        logWindow.addText(tabName,content);
                    }
                }
                if(len>0) {
                    Thread.sleep(200);
                } else {
                    Thread.sleep(1000);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        isRunning=false;
    }
}
