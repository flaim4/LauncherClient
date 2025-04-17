package net.subworld;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.*;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        JFrame frame = new JFrame("HTML Viewer with Click Handling");

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText("<html><body><h1>Hello, World!</h1>" +
                "<p>This is HTML content with a <a href='#' id='link1'>link</a>.</p>" +
                "</body></html>");

        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    String link = e.getDescription();
                    System.out.println(link);
                    if (link.equals("#")) {
                        JOptionPane.showMessageDialog(frame, "Вы кликнули на ссылку!");
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        frame.add(scrollPane);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}