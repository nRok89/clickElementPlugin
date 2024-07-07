package com.click;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class SelectMethodsDialog extends DialogWrapper {
    private final PsiMethod[] methods;
    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private JTextField methodPrefixField;
    private final String prefixKey;
    private static final Preferences prefs = Preferences.userRoot().node("com/click");

    public SelectMethodsDialog(PsiMethod[] methods, String defaultPrefix, String prefixKey) {
        super(true);
        this.methods = methods;
        this.prefixKey = prefixKey;
        String lastPrefix = prefs.get(prefixKey, defaultPrefix);
        this.methodPrefixField = new JTextField(lastPrefix);
        setTitle("Select Methods and Define Execution Order");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel prefixPanel = new JPanel(new BorderLayout());
        prefixPanel.add(new JLabel("Method Prefix: "), BorderLayout.WEST);
        prefixPanel.add(methodPrefixField, BorderLayout.CENTER);

        JPanel methodsPanel = new JPanel(new GridLayout(methods.length, 1));
        for (PsiMethod method : methods) {
            JCheckBox checkBox = new JCheckBox(method.getName());
            checkBoxes.add(checkBox);
            methodsPanel.add(checkBox);
        }

        panel.add(prefixPanel, BorderLayout.NORTH);
        panel.add(methodsPanel, BorderLayout.CENTER);

        return panel;
    }

    public List<PsiMethod> getSelectedMethods() {
        List<PsiMethod> selectedMethods = new ArrayList<>();
        for (int i = 0; i < methods.length; i++) {
            if (checkBoxes.get(i).isSelected()) {
                selectedMethods.add(methods[i]);
            }
        }
        return selectedMethods;
    }

    public String getMethodPrefix() {
        String prefix = methodPrefixField.getText().trim();
        prefs.put(prefixKey, prefix);
        return prefix;
    }
}
