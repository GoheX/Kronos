/*
 * E: Ken@kenreid.co.uk
 * Written by Ken Reid
 */
package gui;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import dataObjects.Window;
import test.Watcher;

/**
 * The Class GUI.
 */
public class GUI {

	/** The data folder item. */
	MenuItem dataFolderItem;

	/** The exit item. */
	MenuItem exitItem;

	/** The git hub item. */
	MenuItem gitHubItem;

	/** The paused. */
	private boolean paused = false;

	/** The pause go. */
	private MenuItem pauseGo;

	/** The pie chart item. */
	MenuItem pieChartItem;

	private SystemTray tray;

	private TrayIcon trayIcon;

	/**
	 * Instantiates a new gui.
	 *
	 * @param runnable
	 *            the runnable
	 * @param telescope
	 *            the telescope
	 */
	@SuppressWarnings("unused")
	public GUI(final Runnable runnable, final Thread telescope) {
		this.setupTrayIcon("src/gui/images/icon.png");

		// if pie chart option is chosen, show pie chart.
		this.pieChartItem.addActionListener(e -> {
			new PieChart(((Watcher) runnable).getWindows());
		});

		// if github item is chosen, open github page
		this.gitHubItem.addActionListener(e -> {
			final String url_open = "https://github.com/GoheX/Kronos";
			try {
				//java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
				Runtime.getRuntime().exec(new String[]{"cmd", "/c","start chrome "+url_open});
			}
			catch (final IOException e1) {
				e1.printStackTrace();
			}
		});

		// Open datafolder
		this.dataFolderItem.addActionListener(e -> {
			try {
				Desktop.getDesktop().open(new File(System.getProperty("user.dir")));
			}
			catch (final IOException e1) {
				e1.printStackTrace();
			}
		});

		// Pause / go
		// this also updates icon
		String iconLocation = "src/gui/images";

		this.pauseGo.addActionListener(e -> {
			if (this.paused == false) {
				Image image = null;
				try {
					File pathToFile = new File(iconLocation + "/pausedIcon.png");
					FileInputStream fis = new FileInputStream(pathToFile);
					image = ImageIO.read(fis);
				}
				catch(Exception ee){
					System.err.println(ee);
				}
				//final Image image = this.createImage(, "tray icon");
				this.trayIcon.setImage(image);
				((Watcher) runnable).resume();
				this.paused = false;
			}
			else {
				Image image = null;
				try {
					File pathToFile = new File(iconLocation + "/icon.png");
					FileInputStream fis = new FileInputStream(pathToFile);
					image = ImageIO.read(fis);
				}
				catch(Exception ee){
					System.err.println(ee);
				}
				//final Image image = this.createImage(, "tray icon");
				this.trayIcon.setImage(image);
				((Watcher) runnable).pause();
				this.paused = true;
			}
		});

		// Exit
		this.exitItem.addActionListener(e -> {
			this.tray.remove(this.trayIcon);
			System.exit(0);
		});
	}

	/**
	 * Creates the image.
	 *
	 * @param path
	 *            the path
	 * @param description
	 *            the description
	 * @return the image
	 */
	protected Image createImage(final String path, final String description) {
		URL imageURL = null;
		try {
			imageURL = new File(path).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		}
		final Image imageIcon = new ImageIcon(imageURL, description).getImage();
		return imageIcon;
	}

	private void setupTrayIcon(final String iconLocation) {
		// Create a pop-up menu components
		this.pieChartItem = new MenuItem("View Pie Chart");
		this.gitHubItem = new MenuItem("GitHub Page");
		this.dataFolderItem = new MenuItem("Open Data Folder");
		this.pauseGo = new MenuItem("Pause / Go");
		this.exitItem = new MenuItem("Exit");

		// if mac or unix etc
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();
		this.trayIcon = new TrayIcon(this.createImage(iconLocation, "tray icon"));
		this.trayIcon.setImageAutoSize(true);
		this.tray = SystemTray.getSystemTray();

		// Add components to pop-up menu
		popup.add(this.pieChartItem);
		popup.add(this.gitHubItem);
		popup.add(this.dataFolderItem);
		popup.addSeparator();
		popup.add(this.pauseGo);
		popup.addSeparator();
		popup.add(this.exitItem);

		this.trayIcon.setPopupMenu(popup);

		try {
			this.tray.add(this.trayIcon);
		}
		catch (final AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}

	}

}
