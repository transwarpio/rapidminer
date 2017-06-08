package com.rapidminer.core.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by fan on 17-3-12.
 */
public class LoginButton extends JButton {
    /** the owner of this button */
    private Window owner;
    public LoginButton(final Window owner, String label) {
        super(label);
        this.owner = owner;
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ConfigurablePasswordDialog passwordDialog = new ConfigurablePasswordDialog(owner);
                passwordDialog.setVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

}
