# Woolly

<img height="128" src="src/main/resources/META-INF/pluginIcon.svg" width="128"/>

<p>Woolly is a simple Code Generation IntelliJ plugin that leverages Large Language Models (LLMs) to seamlessly generate,
    refactor, or simplify code.</p>

<h3>Key Features:</h3>

<ul>
    <li>
        <strong>Code Refactoring:</strong>
        <ul>
            <li>The plugin excels in refactoring code to improve readability, maintainability, and overall code
                quality.</li>
            <li>It intelligently analyzes the provided code snippets and suggests optimized versions, eliminating
                redundancies and enhancing efficiency.</li>
        </ul>
    </li>
    <li>
        <strong>Code Generation:</strong>
        <ul>
            <li>Generate new code tailored to your specifications.</li>
        </ul>
    </li>
</ul>

<h3>Set-Up:</h3>

<p>Go to <strong>Settings</strong> &gt; <strong>Other Settings</strong> &gt; <strong>Woolly Settings</strong> and configure the URL for the LLM you would like to use,
    the model, and optionally, your API key.</p>

<p>If using Ollama, you need to pull a model (<a href="https://ollama.com/library" target="_blank">https://ollama.com/library</a>)
    before using it in the plugin.</p>

<h3>Usage:</h3>

<p>Select a piece of text, right-click, and choose the magic Woollify action. In its default form, the action will
    try to simplify/refactor the code. However, it can also generate code. You can instruct it via comments. Feel
    free to experiment.</p>

<p>If you are not satisfied with the result, you can always Undo the changes.</p>

<p>Have fun!</p>
