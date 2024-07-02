package com.click;


import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SelectFieldsDialog extends DialogWrapper {
    private final PsiField[] fields;
    private final List<JCheckBox> checkBoxes = new ArrayList<>();

    public SelectFieldsDialog(PsiField[] fields) {
        super(true);
        this.fields = fields;
        setTitle("Select Fields To Create Methods");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(fields.length, 1));
        for (PsiField field : fields) {
            JCheckBox checkBox = new JCheckBox(field.getName());
            checkBoxes.add(checkBox);
            panel.add(checkBox);
        }
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
}