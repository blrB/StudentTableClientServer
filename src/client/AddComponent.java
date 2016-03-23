package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by andrey on 11/03/16.
 */
public class AddComponent {

    private static final String IMG_PATCH = "img/";
    private static final Insets insets = new Insets(0, 0, 0, 0);

    public static void add(Container container, Component component,
                                     int gridx, int gridy, int gridwidth, int gridheight) {
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0);
        container.add(component, gbc);
    }

    public static void add(Container container, String nameLabel,
                                     int gridx, int gridy, int gridwidth, int gridheight) {
        JLabel label = new JLabel(nameLabel);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        label.setHorizontalAlignment(JLabel.CENTER);
        GridBagConstraints gbc = new GridBagConstraints(gridx, gridy, gridwidth, gridheight, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0);
        container.add(label, gbc);
    }

    public static JButton makeButton(JButton button, String imgString, ActionListener action){
        button.addActionListener(action);
        String patch = IMG_PATCH + imgString;
        ImageIcon img = new ImageIcon(patch);
        button.setIcon(img);
        return button;
    }
}
