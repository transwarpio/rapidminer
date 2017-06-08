package com.rapidminer.launcher;

/**
 * Created by mk on 3/10/16.
 */
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

final class RebootAnnouncer {
    private RebootAnnouncer() {
    }

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (InstantiationException ex) {

        } catch(IllegalAccessException ex) {

        } catch(UnsupportedLookAndFeelException ex) {

        } catch(ClassNotFoundException ex){ }

        JFrame frame = new JFrame("RapidMiner Updater");
        ImageIcon img = new ImageIcon(RebootAnnouncer.class.getResource("/rapidminer_frame_icon_128.png"));
        frame.setIconImage(img.getImage());
        JPanel contentPanel = new JPanel(new GridBagLayout());
        String message = "";
        if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            message = "<html><div style=\"width:400px\"><p style=\"font-weight:bold;margin-bottom:10px;\">This update of RapidMiner requires a system reboot.</p><p style=\"font-weight:normal;\">You can either reboot now or, if that is not possible, start RapidMiner Studio via the file <em>RapidMiner-Studio.bat</em> inside your RapidMiner Studio installation directory.</p></div></html>";
        } else {
            message = "<html><div style=\"width:400px\"><p style=\"font-weight:bold;margin-bottom:10px;\">Please restart RapidMiner manually for the update changes to take effect.</p></div></html>";
        }

        JLabel label = new JLabel();
        label.setText(message);
        label.setIcon(new ImageIcon(RebootAnnouncer.class.getResource("/information.png")));
        label.setIconTextGap(24);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 0;
        contentPanel.add(label, gbc);
        JButton okayButton = new JButton("Okay");
        okayButton.setMnemonic('O');
        okayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        contentPanel.add(okayButton, gbc);
        frame.setAlwaysOnTop(true);
        frame.setContentPane(contentPanel);
        frame.setDefaultCloseOperation(3);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();
    }
}
