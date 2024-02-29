# Woolly

<img height="128" src="src/main/resources/META-INF/pluginIcon.svg" width="128"/>

[Woolly](https://plugins.jetbrains.com/plugin/23836-woolly?noRedirect=true) is a simple Code Generation IntelliJ plugin that utilizes Large Language Models (LLMs) to generate, refactor, or
simplify code.

### Key Features:

- **Code Refactoring:**

    - The plugin excels in refactoring code for improved readability, maintainability, and overall code quality.
    - It intelligently analyzes provided code snippets and suggests optimized versions, eliminating redundancies and
      enhancing efficiency.

- **Code Generation:**

    - Generate new code tailored to your specifications.

### Set-Up:

1. Go to **Settings** > **Other Settings** > **Woolly Settings** and configure the URL for the desired LLM, model, and
   optionally, your API key.
2. If using Ollama, pull a model (<a href="https://ollama.com/library" target="_blank">https://ollama.com/library</a>)
   before using it in the plugin.

### Usage:

1. Select a piece of text, right-click, and choose the magic **Woollify** action. Its default form will attempt to simplify
   or refactor the code.
2. Generate code by instructing the plugin via comments. Feel free to experiment.
3. If not satisfied with the result, undo the changes.
4. Enjoy!
