import ing.llamaz.woolly.OpenAI;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

public class TestActions {

    @Test
    public void testWoolify() {

        OpenAI openai = new OpenAI() {
            @Override
            protected @NotNull String getProperty(String key, String defaultValue) {
                return defaultValue;
            }
        };

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
                         * @param word The word to check for palindrome property
                         * @return True if the word is a palindrome, false otherwise
                         */
                        """)
        );

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
                       for (int i = 1; i <= 6; i++) {
                           System.out.println(i);
                       }
                       """));

        assertThat(
                openai.woolify("", """
                        int sum = 0;
                        for (int i = 1; i <= 5; i++) {
                            sum += i;
                        }
                        System.out.println("Sum is: " + sum);
                        """, "java"),
                equalToIgnoringWhiteSpace("""
                        int sum = 0;
                        for (int i = 1; i <= 5; i++) {
                            sum += i;
                        }
                        System.out.println("Sum is: " + sum);
                        """));

    }

}
