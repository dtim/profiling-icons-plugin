package com.comitative.pic.statistics;

import com.comitative.pic.parsers.SnapshotParser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class LoadStatisticsAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(LoadStatisticsAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project currentProject = e.getProject();
        if (currentProject != null) {
            StatisticsService service = currentProject.getService(StatisticsService.class);
            if (service != null) {
                List<SnapshotParser> parserList = service.getParserList();
                if (!parserList.isEmpty()) {
                    ReportLoaderDialog dialog = new ReportLoaderDialog(
                            currentProject,
                            parserList.toArray(new SnapshotParser[0]));
                    if (dialog.showAndGet()) {
                        File selectedFile = dialog.getFile();
                        SnapshotParser parser = dialog.getParser();
                        if (service.loadFile(selectedFile, parser)) {
                            LOG.info("Loaded the profiler snapshot " + selectedFile.getName());
                        } else {
                            LOG.warn("Unable to load the profiler snapshot " + selectedFile.getName());
                            Messages.showMessageDialog(
                                    currentProject,
                                    "Unable to load the profiler snapshot",
                                    "Load failed",
                                    Messages.getErrorIcon());
                        }
                    } else {
                        LOG.info("User decided not to load a snapshot");
                    }
                } else {
                    LOG.error("No profiler snapshot parsers registered");
                }
            } else {
                LOG.error("Statistics service not found");
            }
        } else {
            LOG.warn("Unable to load profiler snapshots when no project is active");
        }
    }
}
