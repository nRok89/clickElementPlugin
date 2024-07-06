package com.click;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ClickAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            return;
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return;
        }

        PsiClass psiClass = getPsiClassFromFile(psiFile);
        if (psiClass == null) {
            return;
        }

        // Filtrowanie pól typu WebElement i bez istniejących metod klikających
        PsiField[] fields = getWebElementFields(psiClass);
        if (fields.length == 0) {
            Messages.showMessageDialog(project, "No WebElement fields found or all fields already have click methods.", "Info", Messages.getInformationIcon());
            return;
        }

        SelectFieldsDialog dialog = new SelectFieldsDialog(fields);
        if (dialog.showAndGet()) {
            List<PsiField> selectedFields = dialog.getSelectedFields();
            generateClickMethods(project, psiClass, selectedFields);
        }
    }

    private PsiClass getPsiClassFromFile(PsiFile psiFile) {
        for (PsiElement element : psiFile.getChildren()) {
            if (element instanceof PsiClass) {
                return (PsiClass) element;
            }
        }
        return PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
    }

    private PsiField[] getWebElementFields(PsiClass psiClass) {
        return Arrays.stream(psiClass.getFields())
                .filter(field -> isWebElement(field) && !hasClickMethod(psiClass, field))
                .toArray(PsiField[]::new);
    }

    private boolean isWebElement(PsiField field) {
        PsiType type = field.getType();
        return type.getCanonicalText().equals("org.openqa.selenium.WebElement");
    }

    private boolean hasClickMethod(PsiClass psiClass, PsiField field) {
        String methodName = "click" + capitalizeFirstLetter(field.getName());
        return Arrays.stream(psiClass.getMethods())
                .anyMatch(method -> method.getName().equals(methodName));
    }

    private void generateClickMethods(Project project, PsiClass psiClass, List<PsiField> selectedFields) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

            for (PsiField field : selectedFields) {
                String fieldName = field.getName();
                String capitalizedFieldName = capitalizeFirstLetter(fieldName);

                String clickMethod = "public " + psiClass.getName() + " click" + capitalizedFieldName + "() {\n" +
                        "    " + "clickElement(" + fieldName + ");\n" +
                        "return this;\n" +
                        "}\n";

                try {
                    psiClass.add(elementFactory.createMethodFromText(clickMethod, psiClass));
                } catch (IncorrectOperationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}