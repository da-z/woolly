package ing.llamaz.woolly;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgSystem;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenAI {

    private static final Logger log = LoggerFactory.getLogger(OpenAI.class);

    public static final String DEFAULT_BASE_URL = "http://localhost:11434";
    public static final String DEFAULT_MODEL = "mixtral:8x7b-instruct-v0.1-q8_0";

    private String model = DEFAULT_MODEL;
    private String baseUrl = DEFAULT_BASE_URL;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private OpenAI() {
    }

    private static final class InstanceHolder {
        private static final OpenAI instance = new OpenAI();
    }

    public static OpenAI getInstance() {
        return InstanceHolder.instance;
    }

    private SimpleOpenAI getClient() {
        return SimpleOpenAI.builder().baseUrl(this.baseUrl).apiKey("dummy").build();
    }

    public String woolify(String context, String snippet, String language) {

        String sys = """
                You are an AI plugin that generates, fixes or simplifies %s code.
                If the code is already simple and there is nothing more that can be done, reply with empty text.
                When there are many variants to choose from, you choose the most appropriate response.
                You reply with just the code. No other explanations are necessary.
                """.formatted(language);

        String user = snippet;
        if (context != null && !context.isBlank()) {
            user = """
                For your reference, this is what the file currently looks like:
                
                %s
                
                Now, please modify following piece of code to fit the context:
                
                %s
                """.formatted(context, snippet);
        }

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

        return extractCodeBlock(response);
    }

    static Pattern codeBlockPattern = Pattern.compile("```.*?\n(.*?)```", Pattern.DOTALL);

    public static String extractCodeBlock(String text) {
        Matcher matcher = codeBlockPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return null;
        }
    }

}
