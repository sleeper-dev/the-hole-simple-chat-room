import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextListener implements DocumentListener {
    JTextField textField;
    JButton button;

    public TextListener(JTextField textField, JButton button){
        this.textField = textField;
        this.button = button;
    }

    public void changedUpdate(DocumentEvent e) {}

    public void removeUpdate(DocumentEvent e) {
        button.setEnabled(!textField.getText().trim().isEmpty());
    }
    public void insertUpdate(DocumentEvent e) {
        button.setEnabled(!textField.getText().trim().isEmpty());
    }

}