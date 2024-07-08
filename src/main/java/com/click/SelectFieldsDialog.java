package com.click;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class SelectFieldsDialog extends DialogWrapper {
    private final PsiField[] fields;
    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private JTextField methodPrefixField;
    private final String prefixKey;
    private static final Preferences prefs = Preferences.userRoot().node("com/click");

    public SelectFieldsDialog(PsiField[] fields, String defaultPrefix, String prefixKey) {
        super(true);
        this.fields = fields;
        this.prefixKey = prefixKey;
        String lastPrefix = prefs.get(prefixKey, defaultPrefix);
        this.methodPrefixField = new JTextField(lastPrefix);
        setTitle("Select Fields To Create Methods");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel prefixPanel = new JPanel(new BorderLayout());
        prefixPanel.add(new JLabel("Method Prefix: "), BorderLayout.WEST);
        prefixPanel.add(methodPrefixField, BorderLayout.CENTER);

        JPanel fieldsPanel = new JPanel(new GridLayout(fields.length, 1));
        for (PsiField field : fields) {
            JCheckBox checkBox = new JCheckBox(field.getName());
            checkBoxes.add(checkBox);
            fieldsPanel.add(checkBox);
        }

        panel.add(prefixPanel, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);

        return panel;
    }

    public List<PsiField> getSelectedFields() {
        List<PsiField> selectedFields = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            if (checkBoxes.get(i).isSelected()) {
                selectedFields.add(fields[i]);
            }
        }
        return selectedFields;
    }

    public String getMethodPrefix() {
        String prefix = methodPrefixField.getText().trim();
        prefs.put(prefixKey, prefix);
        return prefix;
    }
}
