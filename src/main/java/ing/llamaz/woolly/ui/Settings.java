package ing.llamaz.woolly.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import ing.llamaz.woolly.OpenAI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Settings implements Configurable {

    public static final String BASE_URL_KEY = "ing.llamaz.woolly.settings.baseUrl";
    public static final String API_KEY = "ing.llamaz.woolly.settings.apiKey";
    public static final String MODEL_KEY = "ing.llamaz.woolly.settings.model";

    private final JPanel settingsPanel;
    private final JTextField baseUrlField;
    private final JPasswordField apiKeyField;
    private final JTextField modelField;

    public Settings() {
        settingsPanel = new JPanel();

        JLabel baseUrlLabel = new JLabel("Base URL:");
        baseUrlField = new JTextField();

        JLabel apiKeyLabel = new JLabel("API Key (optional):");
        apiKeyField = new JPasswordField();

        JLabel modelLabel = new JLabel("Model:");
        modelField = new JTextField();

        // Create layout
        GroupLayout layout = new GroupLayout(settingsPanel);
        settingsPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Set horizontal alignment
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(baseUrlLabel)
                        .addComponent(apiKeyLabel)
                        .addComponent(modelLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(baseUrlField)
                        .addComponent(apiKeyField)
                        .addComponent(modelField)));

        // Set vertical alignment
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(baseUrlLabel)
                        .addComponent(baseUrlField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(apiKeyLabel)
                        .addComponent(apiKeyField))  // Added apiKeyPasswordField here
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(modelLabel)
                        .addComponent(modelField)));

    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Woolly Settings";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return settingsPanel;
    }

    @Override
    public boolean isModified() {
        return !baseUrlField.getText().equals(getBaseUrl())
                || !new String(apiKeyField.getPassword()).equals(getApiKey())
                || !modelField.getText().equals(getModel());
    }

    @Override
    public void apply() {
        setBaseUrl(baseUrlField.getText());
        setApiKey(new String(apiKeyField.getPassword()));
        setModel(modelField.getText());
    }

    @Override
    public void reset() {
        baseUrlField.setText(getBaseUrl());
        apiKeyField.setText(getApiKey());
        modelField.setText(getModel());
    }

    private String getBaseUrl() {
        return PropertiesComponent.getInstance().getValue(BASE_URL_KEY, OpenAI.getInstance().getBaseUrl());
    }

    private void setBaseUrl(String baseUrl) {
        PropertiesComponent.getInstance().setValue(BASE_URL_KEY, baseUrl);
        OpenAI.getInstance().setBaseUrl(baseUrl);
    }

    private String getApiKey() {
        return PropertiesComponent.getInstance().getValue(API_KEY, OpenAI.getInstance().getApiKey());
    }

    private void setApiKey(String apiKey) {
        PropertiesComponent.getInstance().setValue(API_KEY, apiKey);
        OpenAI.getInstance().setApiKey(apiKey);
    }

    private String getModel() {
        return PropertiesComponent.getInstance().getValue(MODEL_KEY, OpenAI.getInstance().getModel());
    }

    private void setModel(String model) {
        PropertiesComponent.getInstance().setValue(MODEL_KEY, model);
        OpenAI.getInstance().setModel(model);
    }

}
