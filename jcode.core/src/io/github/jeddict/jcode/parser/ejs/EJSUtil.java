/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.jcode.parser.ejs;

import io.github.jeddict.jcode.console.Console;
import static io.github.jeddict.jcode.console.Console.FG_BLUE;
import static io.github.jeddict.jcode.console.Console.FG_DARK_RED;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import static io.github.jeddict.jcode.util.FileUtil.loadResource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta
 */
public class EJSUtil {

    public static void copyDynamicResource(Consumer<FileTypeStream> parserManager, String inputResource, FileObject webRoot, Function<String, String> pathResolver, ProgressHandler handler) throws IOException {
        InputStream inputStream = loadResource(inputResource);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if(entry.isDirectory()){
                    continue;
                }
                boolean skipParsing = true;
                String entryName = entry.getName();                
                if (entryName.endsWith(".ejs")) {
                    skipParsing = false;
                    entryName = entryName.substring(0, entryName.lastIndexOf("."));
                }
                String targetPath = pathResolver.apply(entryName);
                if (targetPath == null) {
                    continue;
                }
                handler.progress(targetPath);
                FileObject target = org.openide.filesystems.FileUtil.createData(webRoot, targetPath);
                FileLock lock = target.lock();
                try {
                    OutputStream outputStream = target.getOutputStream(lock);
                    parserManager.accept(new FileTypeStream(entryName, zipInputStream, outputStream, skipParsing));
                    zipInputStream.closeEntry();
                } finally {
                    lock.releaseLock();
                }
            }
        } catch (Throwable ex) {
            Exceptions.printStackTrace(ex);
            System.out.println("InputResource : " + inputResource);
        }
    }

    public static void insertNeedle(FileObject root, String source, String needlePointer, String needleContent, ProgressHandler handler) {
        if(StringUtils.isEmpty(needleContent)){
            return;
        }
        Charset charset = Charset.forName("UTF-8");
        BufferedReader reader;
        BufferedWriter writer;
        if (source.endsWith("json")) {
            needlePointer = "\"" + needlePointer + "\"";
        } else {
            needlePointer = " " + needlePointer + " ";
        }
        try {
            // temp file
            File outFile = File.createTempFile("needle", "tmp");
            // input
            FileObject sourceFileObject = root.getFileObject(source);
            if (sourceFileObject == null) {
                handler.error("Needle file", String.format("needle file '%s' not found ", source));
                return;
            }
            File sourceFile = FileUtil.toFile(sourceFileObject);
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), charset));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), charset));
            StringBuilder content = new StringBuilder();
            boolean contentUpdated = false;
            String thisLine;
            while ((thisLine = reader.readLine()) != null) {
                if (thisLine.contains(needlePointer)) {
                    content.append(needleContent);
                    contentUpdated = true;
                }
                content.append(thisLine).append("\n");
            }
            
            IOUtils.write(content.toString(), writer);
            
            try {
                    reader.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                    writer.flush();
                    writer.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (contentUpdated) {
                sourceFile.delete();
                outFile.renameTo(sourceFile);
            } else {
                outFile.delete();
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } 
    }

    public static void copyDynamicFile(Consumer<FileTypeStream> parserManager, String inputResource, FileObject webRoot, String targetFile, ProgressHandler handler) throws IOException {
        try {
            handler.progress(targetFile);
            FileObject target = org.openide.filesystems.FileUtil.createData(webRoot, targetFile);
            FileLock lock = target.lock();
            try {
                InputStream inputStream = loadResource(inputResource);
                OutputStream outputStream = target.getOutputStream(lock);
                parserManager.accept(new FileTypeStream(inputResource, inputStream, outputStream));
            } finally {
                lock.releaseLock();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static Map<String, String> getResource(String inputResource) {
        Map<String, String> data = new HashMap<>();
        InputStream inputStream = loadResource(inputResource);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().lastIndexOf('.') == -1) {
                    continue;
                }
                StringWriter writer = new StringWriter();
                IOUtils.copy(zipInputStream, writer, StandardCharsets.UTF_8.name());
                String fileName = entry.getName();
                fileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                data.put(fileName, writer.toString());
                zipInputStream.closeEntry();
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            System.out.println("InputResource : " + inputResource);
        }
        return data;
    }

    public static void executeCommand(FileObject workingFolder, ProgressHandler handler, String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);

//                Map<String, String> env = pb.environment();
            // If you want clean environment, call env.clear() first
//                env.put("VAR1", "myValue");
//                env.remove("OTHERVAR");
//                env.put("VAR2", env.get("VAR1") + "suffix");
            pb.directory(FileUtil.toFile(workingFolder));
            Process proc = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            String s;
            while ((s = stdInput.readLine()) != null) {
                handler.append(Console.wrap(s, FG_BLUE));
            }

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                handler.append(Console.wrap(s, FG_DARK_RED));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
