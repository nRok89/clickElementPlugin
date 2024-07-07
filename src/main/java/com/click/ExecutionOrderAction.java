package com.click;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExecutionOrderAction extends AnAction {
    private static final String PREFIX_KEY = "ExecutionOrderMethodPrefix";

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

        // Pobranie istniejących metod z klasy
        PsiMethod[] methods = psiClass.getMethods();
        if (methods.length == 0) {
            Messages.showMessageDialog(project, "No methods found in the class.", "Info", Messages.getInformationIcon());
            return;
        }

        // Wyświetlenie okna dialogowego do wyboru metod
        SelectMethodsDialog dialog = new SelectMethodsDialog(methods, "execute", PREFIX_KEY);
        if (dialog.showAndGet()) {
            List<PsiMethod> selectedMethods = dialog.getSelectedMethods();
            if (selectedMethods.size() > 1) {
                // Jeżeli wybrano więcej niż jedną metodę, przejdź do okna dialogowego do ustawienia kolejności
                OrderMethodsDialog orderMethodsDialog = new OrderMethodsDialog(selectedMethods);
                if (orderMethodsDialog.showAndGet()) {
                    List<PsiMethod> orderedMethods = orderMethodsDialog.getOrderedMethods();
                    String methodPrefix = dialog.getMethodPrefix();
                    generateExecutionOrderMethod(project, editor, psiFile, orderedMethods, methodPrefix);
                }
            } else if (selectedMethods.size() == 1) {
                // Jeśli wybrano tylko jedną metodę, automatycznie tworzymy metodę
                String methodPrefix = dialog.getMethodPrefix();
                generateExecutionOrderMethod(project, editor, psiFile, selectedMethods, methodPrefix);
            }
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

    private void generateExecutionOrderMethod(Project project, Editor editor, PsiFile psiFile, List<PsiMethod> selectedMethods, String methodPrefix) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);

            CaretModel caretModel = editor.getCaretModel();
            LogicalPosition logicalPosition = caretModel.getLogicalPosition();
            int offset = editor.logicalPositionToOffset(logicalPosition);

            PsiElement elementAtCaret = psiFile.findElementAt(offset);
            PsiClass targetClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);
            if (targetClass == null) {
                targetClass = getPsiClassFromFile(psiFile);
            }

            PsiElement anchor = elementAtCaret;

            StringBuilder methodBody = new StringBuilder();
            for (PsiMethod method : selectedMethods) {
                String methodName = method.getName();
                methodBody.append("    ").append(methodName).append("();\n");
            }

            String executionOrderMethod = "public " + targetClass.getName() + " " + methodPrefix + "() {\n" +
                    methodBody.toString() +
                    "    return this;\n" +
                    "}\n";

            try {
                PsiMethod newMethod = elementFactory.createMethodFromText(executionOrderMethod, targetClass);
                PsiElement addedMethod = targetClass.addAfter(newMethod, anchor);
                anchor = addedMethod; // Aktualizacja anchor do nowo dodanej metody
            } catch (IncorrectOperationException e) {
                e.printStackTrace();
            }
        });
    }
}
