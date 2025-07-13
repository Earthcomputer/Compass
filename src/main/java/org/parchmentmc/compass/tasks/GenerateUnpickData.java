package org.parchmentmc.compass.tasks;

import com.google.common.io.Files;
import daomephsta.unpick.constantmappers.datadriven.parser.v3.UnpickV3Reader;
import daomephsta.unpick.constantmappers.datadriven.parser.v3.UnpickV3Writer;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class GenerateUnpickData extends DefaultTask {
    @TaskAction
    public void run() throws IOException {
        List<File> files = new ArrayList<>(getInputDirectory().getAsFileTree().getFiles());
        files.sort(Comparator.comparing(File::getName));

        UnpickV3Writer writer = new UnpickV3Writer();
        for (File file : files) {
            if (!file.getName().endsWith(".unpick")) {
                continue;
            }

            try (UnpickV3Reader reader = new UnpickV3Reader(new FileReader(file))) {
                reader.accept(writer);
            }
        }

        Files.asCharSink(getOutput().get().getAsFile(), StandardCharsets.UTF_8)
            .write(writer.getOutput().replace(System.lineSeparator(), "\n"));
    }

    @InputDirectory
    public abstract DirectoryProperty getInputDirectory();

    @OutputFile
    public abstract RegularFileProperty getOutput();
}
