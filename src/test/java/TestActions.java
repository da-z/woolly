import ing.llamaz.woolly.OpenAI;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.isEmptyOrNullString;

public class TestActions {

    OpenAI openai = new OpenAI() {
        @Override
        protected @NotNull String getProperty(String key, String defaultValue) {
            return defaultValue;
        }
    };

    @Test
    public void test1() {
        assertThat(
                openai.woolify("""
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
                                                
                        // summarize what this class does
                                                
                        public class OpenAI {
                                                 
                            private SimpleOpenAI getClient() {
                                if (openai == null) {
                                    openai = SimpleOpenAI.builder().baseUrl(this.baseUrl).apiKey(apiKey).build();
                                }
                                return openai;
                            }
                                                
                            public String woolify(String context, String snippet, String language) {
                                                
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

                        }
                        """, """
                        // summarize what this class does
                        """, "java"),
                equalToIgnoringWhiteSpace("""
                        /**
                         * This class provides functionality for interacting with the OpenAI API to perform text summarization and code extraction.
                         */
                        """)
        );

    }

    @Test
    public void test2() {
        assertThat(
                openai.woolify("""
                        public class Main {
                            /**
                             *
                             * @param word
                             * @return
                             */
                            public static boolean isPalindrome(String word) {
                                int left = 0;
                                int right = word.length() - 1;
                                
                                while (left < right) {
                                    if (word.charAt(left++) != word.charAt(right--)) {
                                        return false;
                                    }
                                }
                                
                                return true;
                            }
                                                
                            public static void main(String[] args) {
                                String test = "abba";
                                System.out.println("Is \\"" + test + "\\" a palindrome? " + isPalindrome(test));
                            }
                        }
                        """, """
                        /**
                         *
                         * @param word
                         * @return
                         */
                        """, "java"),
                equalToIgnoringWhiteSpace("""
                        /**
                         * Checks if the given word is a palindrome.
                         * A palindrome is a word that reads the same backward as forward.
                         *
                         * @param word The word to check for palindrome property.
                         * @return True if the word is a palindrome, false otherwise.
                         */
                        """)
        );

    }

    @Test
    public void test3() {
        assertThat(
                openai.woolify("""
                        // some other Java code
                        System.out.println("Hello");
                                                
                        for (int i = 1; i <= 6; i++) {
                            // Press Ctrl+D to start debugging your code. We have set one breakpoint
                            // for you, but you can always add more by pressing Cmd+F8.
                            System.out.println("i = " + i);
                        """, """
                        for (int i = 1; i <= 6; i++) {
                            // Press Ctrl+D to start debugging your code. We have set one breakpoint
                            // for you, but you can always add more by pressing Cmd+F8.
                            System.out.println("i = " + i);
                        }""", "java"),
                equalToIgnoringWhiteSpace("""
                        // Prints numbers from 1 to 6
                        for (int i = 1; i <= 6; i++) {
                            System.out.println("i = " + i);
                        }
                        """));
    }

    @Test
    public void test4() {
        assertThat(
                openai.woolify("", """
                        System.out.println(1);
                        System.out.println(2);
                        System.out.println(3);
                        System.out.println(4);
                        System.out.println(5);
                        System.out.println(6);
                        """, "java"),
                equalToIgnoringWhiteSpace("""
                        // Prints the numbers 1 to 6
                        for (int i = 1; i <= 6; i++) {
                            System.out.println(i);
                        }
                        """));
    }

    @Test
    public void test5() {
        assertThat(
                openai.woolify("", """
                        int sum = 0;
                        for (int i = 1; i <= 5; i++) {
                            sum += i;
                        }
                        System.out.println("Sum is: " + sum);
                        """, "java"),
                equalToIgnoringWhiteSpace("""
                        // Calculates the sum of numbers from 1 to 5
                        int sum = 0;
                        for (int i = 1; i <= 5; i++) {
                            sum += i;
                        }
                        System.out.println("Sum is: " + sum);
                        """));
    }

    @Test
    public void test6() {
        assertThat(OpenAI.extract("### BEGIN ###hola### END ###"), equalTo("hola"));
        assertThat(OpenAI.extract("### BEGIN ### \n hola \n ### END ###"), equalTo("hola"));
        assertThat(OpenAI.extract("### BEGIN ###```go hola ```### END ###"), equalTo("```go hola ```"));
        assertThat(OpenAI.extract("```go hola```"), equalTo("hola"));
        assertThat(OpenAI.extract("```go hola"), equalTo("hola"));
        assertThat(OpenAI.extract("``` hola```"), equalTo("hola"));
        assertThat(OpenAI.extract("``` hola"), equalTo("hola"));
        assertThat(OpenAI.extract("` hola`"), equalTo("hola"));
        assertThat(OpenAI.extract(" hola "), equalTo("hola"));
        assertThat(OpenAI.extract(" "), isEmptyOrNullString());
    }

}
