import ing.llamaz.woolly.OpenAI;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;

public class TestActions {

    @Before
    public void before() {
        OpenAI.getInstance().setModel("mistral:7b-instruct-v0.2-q8_0");
    }

    @Test
    public void testRefactor() {
        assertThat(
                OpenAI.getInstance().woolify("", """
                        for (int i = 1; i <= 6; i++) {
                            // Press Ctrl+D to start debugging your code. We have set one breakpoint
                            // for you, but you can always add more by pressing Cmd+F8.
                            System.out.println("i = " + i);
                        }""", "java"),
                equalToIgnoringWhiteSpace("""
                        for (int i = 1; i <= 6; i++) {
                            System.out.print("i = " + i);
                            System.out.println();
                        }
                        """));


        assertThat(
                OpenAI.getInstance().woolify("", """
                        System.out.println(1);
                        System.out.println(2);
                        System.out.println(3);
                        System.out.println(4);
                        System.out.println(5);
                        System.out.println(6);
                        """, "java"),
                equalToIgnoringWhiteSpace("""
                        for (int i = 1; i <= 6; i++) { System.out.println(i); }
                        """));

        assertThat(
                OpenAI.getInstance().woolify("", """
                        int sum = 0;
                        for (int i = 1; i <= 5; i++) {
                            sum += i;
                        }
                        System.out.println("Sum is: " + sum);
                        """, "java"),
                equalToIgnoringWhiteSpace("""
                        // Your code looks good and simple. No refactoring needed. // Empty text.
                        """));
    }

}
