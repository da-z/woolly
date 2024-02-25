package ing.llamaz.woolly.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import ing.llamaz.woolly.OpenAI;
import org.jetbrains.annotations.NotNull;

public class WoolifyAction extends BaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String snippet = getSelectedText(e);
        String context = getFileContent(e.getProject());

        // no selected text. send whole file
        if (snippet == null && context != null && !context.isBlank()) {
            String newText = OpenAI.getInstance().woolify("", context, getLanguage(e.getProject()));
            if (newText != null) {
                replaceFileContent(e.getProject(), newText);
            }
            return;
        }

        // send selection
        if (snippet != null && context != null) {
            String newText = OpenAI.getInstance().woolify(context, snippet, getLanguage(e.getProject()));
            if (newText != null) {
                replaceSelectedText(e, newText);
            }
        }
    }

}
