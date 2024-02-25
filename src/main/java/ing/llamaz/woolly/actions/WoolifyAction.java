package ing.llamaz.woolly.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import ing.llamaz.woolly.OpenAI;
import org.jetbrains.annotations.NotNull;

public class WoolifyAction extends BaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String snippet = getSelectedText(e);
        String context = getFileContent(e.getProject());
        String language = getLanguage(e.getProject());

        String title = "Woolifying..";

        // no selected text. send whole file
        if (snippet == null && context != null && !context.isBlank()) {

            runInBackground(e.getProject(), title, () -> OpenAI.getInstance().woolify("", context, language), (result) -> {
                if (result != null) {
                    replaceFileContent(e.getProject(), result);
                }
            });

        }

        // send selection
        if (snippet != null && context != null) {

            runInBackground(e.getProject(), title, () -> OpenAI.getInstance().woolify(context, snippet, language), (result) -> {
                if (result != null) {
                    replaceSelectedText(e, result);
                }
            });

        }

    }

}
