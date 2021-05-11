package com.comitative.pic.statistics;

import com.comitative.pic.parsers.SnapshotParser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.*;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ReportLoaderDialog extends DialogWrapper {
    private final Project project;
    private final SnapshotParser[] parsers;

    private LabeledComponent<TextFieldWithBrowseButton> snapshotSelector;
    private LabeledComponent<ComboBox<SnapshotParser>> parserSelector;

    public ReportLoaderDialog(
            @NotNull Project project,
            @NotNull SnapshotParser[] parsers) {
        super(project);
        this.project = project;
        this.parsers = parsers;
        init();
        setTitle("Select a profile report");
    }

    public @NotNull
    File getSelectedFile() {
        return new File(snapshotSelector.getComponent().getText());
    }

    public @NotNull
    String getSelectedParserKey() {
        SnapshotParser parser = parserSelector.getComponent().getItem();
        return parser.getKey();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        TextFieldWithBrowseButton snapshotTextField = new TextFieldWithBrowseButton();
        snapshotTextField.addBrowseFolderListener(
                "Profiler Snapshot",
                "Select a profiler snapshot file",
                project,
                FileChooserDescriptorFactory.createSingleFileDescriptor());
        snapshotSelector = new LabeledComponent<>();
        snapshotSelector.setText("Snapshot file");
        snapshotSelector.setComponent(snapshotTextField);

        parserSelector = new LabeledComponent<>();
        parserSelector.setText("Snapshot format");
        parserSelector.setComponent(new ComboBox<>(parsers));

        JPanel dialogPanel = new JPanel(new GridLayout(2, 1));
        dialogPanel.add(snapshotSelector);
        dialogPanel.add(parserSelector);
        return dialogPanel;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        TextFieldWithBrowseButton snapshotTextField = snapshotSelector.getComponent();
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(snapshotTextField.getText());
        if (file == null || !file.exists()) {
            return new ValidationInfo("File is missing or not selected", snapshotTextField);
        }

        ComboBox<SnapshotParser> parserComboBox = parserSelector.getComponent();
        if (parserComboBox.getItem() == null) {
            return new ValidationInfo("Report format should be selected", parserComboBox);
        }

        return null;
    }
}
