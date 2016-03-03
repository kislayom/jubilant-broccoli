/*
 This file is part of Peers, a java SIP softphone.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
 Copyright 2010 Yohann Martineau 
 */
package net.kislay.goasat.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

import net.kislay.goasat.Logger;
import net.kislay.goasat.XmlConfig;
import net.kislay.goasat.sip.Utils;
import static net.kislay.goasat.sip.core.useragent.UserAgent.CONFIG_FILE;
import net.kislay.goasat.sip.syntaxencoding.SipUriSyntaxException;
import net.kislay.goasat.sip.transport.SipRequest;
import net.kislay.goasat.sip.transport.SipResponse;

public class MainFrame implements WindowListener, ActionListener {

    public static FileInputStream filein;
    public static boolean MIC_ON = false;
    public static String filename;
    public static SoundChooserMain choosesound;
    
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel dialerPanel;
    private JTextField uri;
    private JButton actionButton;
    private JLabel statusLabel;
    private EventManager eventManager;
    private Registration registration;
    private Logger logger;
    public static XmlConfig config;

    public static void main(final String[] args) throws FileNotFoundException {
//        config = new XmlConfig(this.peersHome + File.separator
//                + CONFIG_FILE, this.logger);
//        filename = "/home/kislay/Documents/GoAsat/SIPROBOTPHN/media/Pause.raw";
//        filein = new FileInputStream(filename);
        //set luk n feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChooseSound.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChooseSound.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChooseSound.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChooseSound.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }// luk nd feel over

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                choosesound = new SoundChooserMain();
                choosesound.setVisible(true);
                choosesound.AddBut();
                createAndShowGUI(args);
            }
        });
    }

    private static void createAndShowGUI(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        new MainFrame(args);
    }

    public MainFrame(final String[] args) {
        String peersHome = Utils.DEFAULT_PEERS_HOME;

        logger = new Logger(peersHome);

        String title = "";
        if (!Utils.DEFAULT_PEERS_HOME.equals(peersHome)) {
            title = peersHome;
        }
        title += "GoAsat";
        mainFrame = new JFrame(title);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.addWindowListener(this);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        dialerPanel = new JPanel();

        uri = new JTextField("sip:xxxx@192.168.2.230", 15);
        uri.addActionListener(this);

        actionButton = new JButton("Call");
        actionButton.addActionListener(this);

        dialerPanel.add(uri);
        dialerPanel.add(actionButton);
        dialerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabel = new JLabel(title);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        Border border = BorderFactory.createEmptyBorder(0, 2, 2, 2);
        statusLabel.setBorder(border);

        mainPanel.add(dialerPanel);
        mainPanel.add(statusLabel);

        Container contentPane = mainFrame.getContentPane();
        contentPane.add(mainPanel);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        JMenuItem menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic('x');
        menuItem.setActionCommand(EventManager.ACTION_EXIT);

        registration = new Registration(statusLabel, logger);

        Thread thread = new Thread(new Runnable() {

            public void run() {
                String peersHome = Utils.DEFAULT_PEERS_HOME;
                if (args.length > 0) {
                    peersHome = args[0];
                }
                eventManager = new EventManager(MainFrame.this,
                        peersHome, logger);
                try {
                    eventManager.register();
                } catch (SipUriSyntaxException e) {
                    statusLabel.setText(e.getMessage());
                }
            }
        });
        thread.start();

        try {
            while (eventManager == null) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            return;
        }
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu("Edit");
        menu.setMnemonic('E');
        menuItem = new JMenuItem("Account");
        menuItem.setMnemonic('A');
        menuItem.setActionCommand(EventManager.ACTION_ACCOUNT);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuItem = new JMenuItem("Preferences");
        menuItem.setMnemonic('P');
        menuItem.setActionCommand(EventManager.ACTION_PREFERENCES);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu("Help");
        menu.setMnemonic('H');
//        menuItem = new JMenuItem("User manual");
//        menuItem.setMnemonic('D');
//        menuItem.setActionCommand(EventManager.ACTION_DOCUMENTATION);
//        menuItem.addActionListener(eventManager);
//        menu.add(menuItem);
        menuItem = new JMenuItem("About");
        menuItem.setMnemonic('A');
        menuItem.setActionCommand(EventManager.ACTION_ABOUT);
        menuItem.addActionListener(eventManager);
        menu.add(menuItem);
        menuBar.add(menu);

        mainFrame.setJMenuBar(menuBar);

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    // window events
    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
        eventManager.windowClosed();
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    // action event
    @Override
    public void actionPerformed(ActionEvent e) {
        eventManager.callClicked(uri.getText());
    }

    // misc.
    public void setLabelText(String text) {
        statusLabel.setText(text);
        mainFrame.pack();
    }

    public void registerFailed(SipResponse sipResponse) {
        registration.registerFailed();
    }

    public void registerSuccessful(SipResponse sipResponse) {
        registration.registerSuccessful();
    }

    public void registering(SipRequest sipRequest) {
        registration.registerSent();
    }

    public void socketExceptionOnStartup() {
        JOptionPane.showMessageDialog(mainFrame, "peers SIP port "
                + "unavailable, exiting");
        System.exit(1);
    }
}
