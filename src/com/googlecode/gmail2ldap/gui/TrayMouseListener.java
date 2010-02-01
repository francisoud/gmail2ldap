package com.googlecode.gmail2ldap.gui;

import java.awt.event.MouseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrayMouseListener implements java.awt.event.MouseListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void mouseClicked(MouseEvent event) {
		logger.debug("Tray Icon - Mouse clicked!");
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		logger.debug("Tray Icon - Mouse entered!");
	}

	@Override
	public void mouseExited(MouseEvent event) {
		logger.debug("Tray Icon - Mouse exited!");
	}

	@Override
	public void mousePressed(MouseEvent event) {
		logger.debug("Tray Icon - Mouse pressed!");
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		logger.debug("Tray Icon - Mouse released!");
	}

}
