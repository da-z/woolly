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
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class BaseAction extends AnAction {

    String getLanguage(Project project) {
        PsiFile psiFile = getPsiFile(project);
        if (psiFile == null) return null;

        return psiFile.getLanguage().getDisplayName();
    }

    @Nullable
    private static PsiFile getPsiFile(Project project) {
        VirtualFile virtualFile = getVirtualFile(project);
        if (virtualFile == null) {
            return null;
        }

        return PsiManager.getInstance(project).findFile(virtualFile);
    }

    String getFileContent(Project project) {
        Document document = getDocument(project);
        if (document == null) return null;

        return document.getText();
    }

    @Nullable
    private static Document getDocument(Project project) {
        VirtualFile virtualFile = getVirtualFile(project);
        if (virtualFile == null) {
            return null;
        }

        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        return fileDocumentManager.getDocument(virtualFile);
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
                            newText + (selectedText.endsWith("\n") ? "\n" : "")
                    );
                    reformatSelectedText(e.getProject(), editor);
                });
            }
        }
    }

    public void replaceFileContent(Project project, String newContent) {
        VirtualFile virtualFile = getVirtualFile(project);
        if (virtualFile == null) return;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                document.setText(newContent);
            }
        });
    }

    @Nullable
    private static VirtualFile getVirtualFile(Project project) {
        TextEditor textEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor();
        if (textEditor == null) {
            return null;
        }

        return textEditor.getFile();
    }

    private static void reformatSelectedText(Project project, Editor editor) {
        PsiFile psiFile = PsiDocumentManager.getInstance(Objects.requireNonNull(project)).getPsiFile(editor.getDocument());
        if (psiFile != null) {
            CodeStyleManager.getInstance(project).reformatText(psiFile,
                    editor.getSelectionModel().getSelectionStart(),
                    editor.getSelectionModel().getSelectionEnd());
        }
    }

    protected void runInBackground(Project project, String title, Callable<String> callable, Consumer<String> consumer) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            ProgressManager.getInstance().run(new Task.Modal(project, title, true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setIndeterminate(true);
                    indicator.setText("Processing...");
                    try {
                        String result = callable.call();
                        if (!indicator.isCanceled()) {
                            WriteCommandAction.runWriteCommandAction(project, () -> consumer.accept(result));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }, title, true, project);
    }

}
