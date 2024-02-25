package ing.llamaz.woolly.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class BaseAction extends AnAction {

    String getLanguage(Project project) {
        TextEditor textEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor();

        if (textEditor == null) {
            return null;
        }

        VirtualFile virtualFile = textEditor.getFile();
        if (virtualFile == null) {
            return null;
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile == null) {
            return null;
        }

        return psiFile.getLanguage().getDisplayName();
    }

    String getFileContent(Project project) {
        TextEditor textEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor();

        if (textEditor == null) {
            return null;
        }

        VirtualFile virtualFile = textEditor.getFile();
        if (virtualFile == null) {
            return null;
        }

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        Document document = fileDocumentManager.getDocument(virtualFile);

        if (document != null) {
            return document.getText();
        }

        return null;
    }

    static String getSelectedText(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            SelectionModel selectionModel = editor.getSelectionModel();
            String selectedText = selectionModel.getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                return selectedText;
            }
        }
        return null;
    }

    void replaceSelectedText(@NotNull AnActionEvent e, String newText) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            String selectedText = editor.getSelectionModel().getSelectedText();
            if (selectedText != null && !selectedText.isEmpty()) {
                WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                    editor.getDocument().replaceString(
                            editor.getSelectionModel().getSelectionStart(),
                            editor.getSelectionModel().getSelectionEnd(),
                            newText
                    );
                    reformatSelectedText(e.getProject(), editor);
                });
            }
        }
    }

    public void replaceFileContent(Project project, String newContent) {
        TextEditor textEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor();

        if (textEditor == null) {
            return;
        }

        VirtualFile virtualFile = textEditor.getFile();
        if (virtualFile == null) {
            return;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                document.setText(newContent);
            }
        });
    }

    private static void reformatSelectedText(Project project, Editor editor) {
        PsiFile psiFile = PsiDocumentManager.getInstance(Objects.requireNonNull(project)).getPsiFile(editor.getDocument());
        if (psiFile != null) {
            CodeStyleManager.getInstance(project).reformatText(psiFile,
                    editor.getSelectionModel().getSelectionStart(),
                    editor.getSelectionModel().getSelectionEnd());
        }
    }

}
