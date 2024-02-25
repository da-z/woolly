package ing.llamaz.woolly.ui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import ing.llamaz.woolly.OpenAI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Settings implements Configurable {

    private static final String BASE_URL_KEY = "ing.llamaz.woolly.settings.baseUrl";
    private static final String MODEL_KEY = "ing.llamaz.woolly.settings.model";

    private final JPanel settingsPanel;
    private final JTextField baseUrlTextField;
    private final JTextField modelTextField;

    public Settings() {
        settingsPanel = new JPanel();

        JLabel baseUrlLabel = new JLabel("Base URL:");
        baseUrlTextField = new JTextField();

        JLabel modelLabel = new JLabel("Model:");
        modelTextField = new JTextField();

        // add a slider field

        // Create layout
        GroupLayout layout = new GroupLayout(settingsPanel);
        settingsPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Set horizontal alignment
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(baseUrlLabel)
                        .addComponent(modelLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(baseUrlTextField)
                        .addComponent(modelTextField)));

        // Set vertical alignment
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(baseUrlLabel)
                        .addComponent(baseUrlTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(modelLabel)
                        .addComponent(modelTextField)));
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
        return !baseUrlTextField.getText().equals(getBaseUrl())
                || !modelTextField.getText().equals(getModel());
    }

    @Override
    public void apply() {
        setBaseUrl(baseUrlTextField.getText());
        setModel(modelTextField.getText());
    }

    @Override
    public void reset() {
        baseUrlTextField.setText(getBaseUrl());
        modelTextField.setText(getModel());
    }

    private String getBaseUrl() {
        return PropertiesComponent.getInstance().getValue(BASE_URL_KEY, OpenAI.getInstance().getBaseUrl());
    }

    private void setBaseUrl(String baseUrl) {
        PropertiesComponent.getInstance().setValue(BASE_URL_KEY, baseUrl);
        OpenAI.getInstance().setBaseUrl(baseUrl);
    }

    private String getModel() {
        return PropertiesComponent.getInstance().getValue(MODEL_KEY, OpenAI.getInstance().getModel());
    }

    private void setModel(String model) {
        PropertiesComponent.getInstance().setValue(MODEL_KEY, model);
        OpenAI.getInstance().setModel(model);
    }
}
