package ing.llamaz.woolly;

import com.intellij.ide.util.PropertiesComponent;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgSystem;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgUser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ing.llamaz.woolly.ui.Settings.*;

public class OpenAI {

    private static final Logger log = LoggerFactory.getLogger(OpenAI.class);

    private String baseUrl = getProperty(BASE_URL_KEY, "http://localhost:11434");
    private String model = getProperty(MODEL_KEY, "mixtral");
    private String apiKey = getProperty(API_KEY, "");

    private SimpleOpenAI openai;

    protected OpenAI() {
    }

    @NotNull
    protected String getProperty(String key, String defaultValue) {
        return PropertiesComponent.getInstance().getValue(key, defaultValue);
    }

    private static final class InstanceHolder {
        private static final OpenAI instance = new OpenAI();
    }

    public static OpenAI getInstance() {
        return InstanceHolder.instance;
    }

    private SimpleOpenAI getClient() {
        if (openai == null) {
            openai = SimpleOpenAI.builder().baseUrl(this.baseUrl).apiKey(apiKey).build();
        }
        return openai;
    }

    public String getModel() {
        return model;
    }
    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setModel(String model) {
        this.model = model;
        this.openai = null;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        this.openai = null;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.openai = null;
    }

    public String woolify(String context, String snippet, String language) {

        String sys = """
                You are an AI plugin that generates, fixes, simplifies, documents or explains %s code.
                If the piece of code contains instructions, do what the comment says.
                When there are many variants to choose from, you choose the most appropriate response.
                You ALWAYS wrap your response in triple backticks ```.
                If your response is not code, write it as code comment(s).
                If you need to simplify and the code is already simple, reply with empty text.
                You comment code sparingly.
                You only add comments to code when it's necessary for understanding its purpose or behavior.
                You do not re-write comments unless they contain errors or spelling mistakes.
                You do not offer explanations for your code decisions.
                You respond succinctly.
                """.formatted(language);

        String user = """
                Given context:
                
                ### BEGIN ###
                %s
                ### END ###
                
                Please update just the following portion of the text strictly as follows:
                
                    - Either write code or documentation but never both at the same time.
                    - If the text is a template of javadoc, jsdoc, or similar comment block then fill-in documentation.
                    - If the text is code, you fill-in, refactor or simplify it.
                    - If the text contains instructions, try to follow them, including generating code.
                    - If the text is a method or function, you reply with method or function, not the whole file or class context.
                    - When writing class documentation reply with just the documentation header, not the whole file or class.
                
                ### BEGIN ###
                %s
                ### END ###
                
                """.formatted(context, snippet);

        log.info("\nsys:\n\n{}\nuser:\n\n{}", sys, user);

        var chatRequest = ChatRequest.builder()
                .model(this.model)
                .messages(List.of(
                        new ChatMsgSystem(sys),
                        new ChatMsgUser(user)))
                .temperature(.0)
                .seed(0)
                .build();

        String response = getClient().chatCompletions().create(chatRequest).join().firstContent();
        log.info(response);

        return extract(response);
    }

    static Pattern fileBlockPattern = Pattern.compile("### BEGIN ###[\n|\\s](.*?)### END ###", Pattern.DOTALL);
    static Pattern codeBlockPattern = Pattern.compile("```[a-zA-Z0-9\\-]*?[\n|\\s](.*?)```", Pattern.DOTALL);
    static Pattern snippetBlockPattern = Pattern.compile("`(.*?)`", Pattern.DOTALL);

    private static String extract(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String extract(String text) {
        String res = extract(fileBlockPattern, text);
        if (res != null) {
            return res;
        }
        res = extract(codeBlockPattern, text);
        if (res != null) {
            return res;
        }
        return extract(snippetBlockPattern, text);
    }

}
