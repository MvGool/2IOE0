package main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyListener;

import javax.sound.sampled.FloatControl;
import javax.swing.JFrame;
import javax.swing.JPanel;

import user_interface.*;

public class UserInterface {
	public boolean initial = true;
	public boolean close;
	public int width;
	public int height;
	
	private JFrame frame = new JFrame();
	private FrontPage frontPage;
	private SettingsPage settingsPage;
	private PausePage pausePage;
	private JPanel currentPage;
		
	public UserInterface() {
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(null);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
        	@Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                close = true;
            }
        });
        frame.setUndecorated(true);
        frame.setVisible(true);
        
        this.width = frame.getWidth();
        this.height = frame.getHeight();
                
        frontPage = new FrontPage(width, height);
		settingsPage = new SettingsPage(width, height);
		pausePage = new PausePage(width, height);
		currentPage = frontPage;
    }
	
	public void run() {
		if (initial) {
			frame.getContentPane().removeAll();
			frame.getContentPane().add(frontPage);
			frame.revalidate();
			frame.repaint();
			frame.setVisible(true);

			while (!frontPage.startGame && !frontPage.close && !settingsPage.close) {
				if (frontPage.settings) {
					currentPage = settingsPage;
					frame.getContentPane().removeAll();
					frame.getContentPane().add(currentPage);
					frame.revalidate();
					frame.repaint();
					frontPage.settings = !frontPage.settings;
				}
				
				if (settingsPage.frontPage) {
					currentPage = frontPage;
					frame.getContentPane().removeAll();
					frame.getContentPane().add(currentPage);
					frame.revalidate();
					frame.repaint();
					settingsPage.frontPage = !settingsPage.frontPage;
				}
				
				if (Main.clip != null) {
					if (settingsPage.musicToggleON) {
						Main.clip.start();
					} else {
						Main.clip.stop();
					}
				}
				
				if (settingsPage.volumeChange) {
					setVolume(settingsPage.getVolume());
					settingsPage.volumeChange = !settingsPage.volumeChange; 
				}
			}
			
			if (frontPage.close || settingsPage.close) {
				close = true;
			}
			
			frontPage.startGame = false;
			frontPage.close = false;
			settingsPage.close = false;
			
			initial = false;
			
			frame.setVisible(false);
		} else {
			frame.getContentPane().removeAll();
			frame.getContentPane().add(pausePage);
			frame.revalidate();
			frame.repaint();
			frame.setVisible(true);
			
			while (!pausePage.startGame && !pausePage.close && !settingsPage.close) {
				if (pausePage.settings) {
					currentPage = settingsPage;

					frame.getContentPane().removeAll();
					frame.getContentPane().add(currentPage);
					frame.revalidate();
					frame.repaint();
					pausePage.settings = !pausePage.settings;
				}
				
				if (settingsPage.frontPage) {
					currentPage = pausePage;
					frame.getContentPane().removeAll();
					frame.getContentPane().add(currentPage);
					frame.revalidate();
					frame.repaint();
					settingsPage.frontPage = !settingsPage.frontPage;
				}
				
				if (Main.clip != null) {
					if (settingsPage.musicToggleON) {
						Main.clip.start();
					} else {
						Main.clip.stop();
					}
				}
				
				if (settingsPage.volumeChange) {
					setVolume(settingsPage.getVolume());
					settingsPage.volumeChange = !settingsPage.volumeChange; 
				}
			}

			if (pausePage.close || settingsPage.close) {
				close = true;
			}
			
			pausePage.startGame = false;
			pausePage.close = false;
			settingsPage.close = false;
			
			frame.setVisible(false);
		}
	}
	
	public void setVolume(int volume) {
		if (Main.clip != null) {
			FloatControl control = (FloatControl) Main.clip.getControl(FloatControl.Type.MASTER_GAIN);        
		    control.setValue(20f * (float) Math.log10((float) volume / 100f));
		}
	}
	
	public void destroy() {
		frame.dispose();
	}
}
