package de.knewcleus.openradar.gui.status.radio;

import java.io.BufferedInputStream;

import de.knewcleus.openradar.gui.LogWindow;

public class LogWriterThread implements Runnable {

    private Radio radio = null;
    private LogWindow logWindow = null;
    private Process process = null;
    private String tabName = null;
    
    public LogWriterThread(LogWindow logWindow, Radio radio, Process process) {
        this.logWindow = logWindow;
        this.process = process;
        this.radio = radio;
        this.tabName=radio.getKey();
        new Thread(this).start();
    }

    @Override
    public void run() {
        
        byte[] buffer = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(process.getInputStream());
        
        try {
            while(true) {
                int len = bis.read(buffer);
                if(len>0) {
                    String content = new String(buffer,0,len);
                    // System.out.println(content);
                    if(content.contains("rejected") || content.contains("accepted")) {
                        if(content.contains("rejected")) {
                            radio.setConnectedToServer(false);
                        } else if(content.contains("accepted")){
                            radio.setConnectedToServer(true);
                        }
                    }
                    logWindow.addText(tabName,content);
                }
                Thread.sleep(200);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
