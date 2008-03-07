package de.knewcleus.fgfs;


public class Updater extends Thread {
	protected IUpdateable updateable;
	protected long lastt;
	
	protected final long intervalMillis;
	
	public Updater(IUpdateable updateable) {
		this.updateable=updateable;
		this.intervalMillis=100;
	}
	
	public Updater(IUpdateable updateable, long intervalMillis) {
		this.updateable=updateable;
		this.intervalMillis=intervalMillis;
		setDaemon(true);
	}
	
	public void run() {
		lastt=System.nanoTime();
		while (!isInterrupted()) {
			long t=System.nanoTime();
			double dt=(t-lastt)*1.0E-9;
			updateable.update(dt);
			lastt=t;
			try {
				sleep(intervalMillis);
			} catch (InterruptedException e) {
				/* Sleep was interrupted => exit */
				break;
			}
		}
	}

}
