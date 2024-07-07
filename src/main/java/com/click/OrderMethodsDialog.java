package com.click;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrderMethodsDialog extends DialogWrapper {
    private final DefaultListModel<PsiMethod> listModel;
    private final JList<PsiMethod> methodsList;

    public OrderMethodsDialog(List<PsiMethod> methods) {
        super(true);
        setTitle("Order Methods");
        listModel = new DefaultListModel<>();
        methods.forEach(listModel::addElement);
        methodsList = new JList<>(listModel);
        methodsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        methodsList.setVisibleRowCount(-1);
        methodsList.setLayoutOrientation(JList.VERTICAL);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(methodsList), BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        JButton upButton = new JButton("Move Up");
        JButton downButton = new JButton("Move Down");

        upButton.addActionListener(e -> moveSelectedItem(-1));
        downButton.addActionListener(e -> moveSelectedItem(1));

        buttonsPanel.add(upButton);
        buttonsPanel.add(downButton);

        panel.add(buttonsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void moveSelectedItem(int direction) {
        int selectedIndex = methodsList.getSelectedIndex();
        if (selectedIndex == -1) {
            return;
        }
        int newIndex = selectedIndex + direction;
        if (newIndex < 0 || newIndex >= listModel.getSize()) {
            return;
        }
        PsiMethod method = listModel.remove(selectedIndex);
        listModel.add(newIndex, method);
        methodsList.setSelectedIndex(newIndex);
    }

    public List<PsiMethod> getOrderedMethods() {
        List<PsiMethod> orderedMethods = new ArrayList<>();
        for (int i = 0; i < listModel.getSize(); i++) {
            orderedMethods.add(listModel.getElementAt(i));
        }
        return orderedMethods;
    }
}
