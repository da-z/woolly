package ing.llamaz.woolly.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import ing.llamaz.woolly.OpenAI;
import org.jetbrains.annotations.NotNull;

public class WoollifyAction extends BaseAction {

    private final String TITLE = "Woollify";

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        String snippet = getSelectedText(e);
        String context = getFileContent(e.getProject());
        String language = getLanguage(e.getProject());

        // no selected text. send whole file
        if (snippet == null && context != null && !context.isBlank()) {
            runInBackground(e.getProject(), TITLE, () -> OpenAI.getInstance().woolify("", context, language), (result) -> {
                if (result != null) {
                    replaceFileContent(e.getProject(), result);
                }
            });
        }

        // send selection
        if (snippet != null && context != null) {
            runInBackground(e.getProject(), TITLE, () -> OpenAI.getInstance().woolify(context, snippet, language), (result) -> {
                if (result != null) {
                    replaceSelectedText(e, result);
                }
            });
        }

    }

}
