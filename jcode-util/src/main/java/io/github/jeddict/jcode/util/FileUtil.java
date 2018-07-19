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
package io.github.jeddict.jcode.util;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.queries.FileEncodingQuery;
import static io.github.jeddict.jcode.util.JavaSourceHelper.reformat;
import io.github.jeddict.jcode.task.progress.ProgressHandler;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;    

/**
 *
 * @author Gaurav Gupta
 */
public class FileUtil {
    
    private static final Logger LOG = Logger.getLogger(FileUtil.class.getName());
    
    public static URL getResourceURL(String resource) {
           if (resource.startsWith("/")) { // NOI18N
                resource = resource.substring(1);
            }
        return getLoader().getResource(resource);
    }
    
    public static InputStream loadResource(String resource) {
        InputStream inputStream = null;
        try {
            inputStream = getResourceURL(resource).openStream();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return inputStream;
    }
    
    private static volatile Object currentLoader;
    private static Lookup.Result<ClassLoader> loaderQuery = null;
    private static boolean noLoaderWarned = false;

    private static ClassLoader getLoader() {
        Object is = currentLoader;
        if (is instanceof ClassLoader) {
            return (ClassLoader) is;
        }

        currentLoader = Thread.currentThread();

        if (loaderQuery == null) {
            loaderQuery = Lookup.getDefault().lookupResult(ClassLoader.class);
            loaderQuery.addLookupListener((LookupEvent ev) -> {
                LOG.fine("Loader cleared"); // NOI18N
                currentLoader = null;
            });
        }

        Iterator it = loaderQuery.allInstances().iterator();
        if (it.hasNext()) {
            ClassLoader toReturn = (ClassLoader) it.next();
            if (currentLoader == Thread.currentThread()) {
                currentLoader = toReturn;
            }
            LOG.log(Level.FINE, "Loader computed: {0}", currentLoader); // NOI18N
            return toReturn;
        } else {
            if (!noLoaderWarned) {
                noLoaderWarned = true;
                LOG.log(Level.WARNING, "No ClassLoader instance found in {0}", Lookup.getDefault() // NOI18N
                );
            }
            return null;
        }
    }

    public static FileObject getFileObject(InputStream in) throws IOException {
        final File tempFile = File.createTempFile("pom", "xml");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            org.openide.filesystems.FileUtil.copy(in, out);
        }
        return org.openide.filesystems.FileUtil.toFileObject(tempFile);
    }

    public static void copyStaticResource(String inputTemplatePath, FileObject toDir, String targetFolder, ProgressHandler handler) throws IOException {
        InputStream stream = loadResource(inputTemplatePath);
        try (ZipInputStream inputStream = new ZipInputStream(stream)) {
            ZipEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                if (entry.getName().lastIndexOf('.') == -1) { //skip if not file
                    continue;
                }
                String targetPath = StringUtils.isBlank(targetFolder) ? entry.getName() : targetFolder + '/' + entry.getName();
                if (handler != null) {
                    handler.progress(targetPath);
                }
                FileObject target = org.openide.filesystems.FileUtil.createData(toDir, targetPath);
                FileLock lock = target.lock();
                try (OutputStream outputStream = target.getOutputStream(lock)) {
                    for (int c = inputStream.read(); c != -1; c = inputStream.read()) {
                        outputStream.write(c);
                    }
                    inputStream.closeEntry();
                } finally {
                    lock.releaseLock();
                }
            }
        }
    }

    public static FileObject expandTemplate(String inputTemplatePath, FileObject toDir, String toFile, Map<String, Object> params) throws IOException {
        InputStream contentStream = loadResource(inputTemplatePath);
        FileObject outputFile = toDir.getFileObject(toFile);
        if (outputFile == null) {
            outputFile = org.openide.filesystems.FileUtil.createData(toDir, toFile);
        }
        expandTemplate(contentStream, outputFile, params);
        return outputFile;
    }
    
    public static void expandTemplate(String inputTemplatePath, FileObject toFile, Map<String, Object> params) throws IOException {
        InputStream contentStream = io.github.jeddict.jcode.util.FileUtil.loadResource(inputTemplatePath);
        expandTemplate(contentStream, toFile, params);
    }

    public static void expandTemplate(InputStream template, FileObject toFile, Map<String, Object> values) throws IOException {
        Charset targetEncoding = FileEncodingQuery.getEncoding(toFile);
        if(toFile.isLocked()){
            LOG.log(Level.SEVERE, "File {0} is locked", new Object[]{toFile.getName()});
            return;
        }
        FileLock lock = toFile.lock();
        try (Writer writer = new OutputStreamWriter(toFile.getOutputStream(lock), targetEncoding)) {
            expandTemplate(new InputStreamReader(template), writer, values, targetEncoding);
        } finally {
            lock.releaseLock();
        }
        DataObject dob = DataObject.find(toFile);
        if (dob != null) {
            reformat(dob);
        }
    }

    public static void expandTemplate(Reader reader, Writer writer, Map<String, Object> values) throws IOException {
        expandTemplate(reader, writer, values, Charset.defaultCharset());
    }

    public static void expandTemplate(Reader reader, Writer writer, Map<String, Object> values, Charset targetEncoding) throws IOException {
        ScriptEngine eng = getScriptEngine();
        Bindings bind = eng.getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        bind.putAll(values);
        bind.put(ENCODING_PROPERTY_NAME, targetEncoding.name());

        try {
            eng.getContext().setWriter(writer);
            eng.eval(reader);
        } catch (ScriptException ex) {
            throw new IOException(ex);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public static String expandTemplate(String inputTemplatePath, Map<String, Object> values) {
        InputStream contentStream = loadResource(inputTemplatePath);
        StringWriter writer = new StringWriter();
        ScriptEngine eng = getScriptEngine();
        Bindings bind = eng.getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        if (values != null) {
            bind.putAll(values);
        }
        bind.put(ENCODING_PROPERTY_NAME, Charset.defaultCharset().name());
        eng.getContext().setWriter(writer);
        Reader is = new InputStreamReader(contentStream);
        try {
            eng.eval(is);
        } catch (ScriptException ex) {
            Exceptions.printStackTrace(ex);
        }

        return writer.toString();
    }

    /**
     * In-memory template api
     *
     * @param templateContent
     * @param values
     * @return
     */
    public static String expandTemplateContent(String templateContent, Map<String, Object> values) {
        StringWriter writer = new StringWriter();
        ScriptEngine eng = getScriptEngine();
        Bindings bind = eng.getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        if (values != null) {
            bind.putAll(values);
        }
        bind.put(ENCODING_PROPERTY_NAME, Charset.defaultCharset().name());
        eng.getContext().setWriter(writer);
        Reader is = new StringReader(templateContent);
        try {
            eng.eval(is);
        } catch (ScriptException ex) {
            Exceptions.printStackTrace(ex);
        }

        return writer.toString();
    }
    
    private static final String ENCODING_PROPERTY_NAME = "encoding"; //NOI18N
    private static ScriptEngineManager manager;

    /**
     * Used core method for getting {@code ScriptEngine} from {@code
     * org.netbeans.modules.templates.ScriptingCreateFromTemplateHandler}.
     */
    private static ScriptEngine getScriptEngine() {
        if (manager == null) {
            synchronized (FileUtil.class) {
                if (manager == null) {
                    ClassLoader loader = Lookup.getDefault().lookup(ClassLoader.class);
                    try {
                        loader.loadClass(PrefixResolver.class.getName());
                    } catch (ClassNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    manager = new ScriptEngineManager(loader != null ? loader : Thread.currentThread().getContextClassLoader());
                }
            }
        }
        return manager.getEngineByName("freemarker");
    }

    public static FileObject createFolder(FileObject folder, String name) throws IOException {
        return org.openide.filesystems.FileUtil.createFolder(folder, name);
    }

    public static FileObject copyFile(String fromFile, FileObject toDir, String toFile) throws IOException {
        MakeFileCopy action = new MakeFileCopy(fromFile, toDir, toFile);
        org.openide.filesystems.FileUtil.runAtomicAction(action);
        if (action.getException() != null) {
            throw action.getException();
        } else {
            return action.getResult();
        }
    }

    private static class MakeFileCopy implements Runnable {

        private final String fromFile;
        private final FileObject toDir;
        private final String toFile;
        private IOException exception;
        private FileObject result;

        MakeFileCopy(String fromFile, FileObject toDir, String toFile) {
            this.fromFile = fromFile;
            this.toDir = toDir;
            this.toFile = toFile;
        }

        IOException getException() {
            return exception;
        }

        FileObject getResult() {
            return result;
        }

        @Override
        public void run() {
            try {
//                if (toDir.getFileObject(toFile) != null) {
//                    return; 
//                }
                FileObject xml = org.openide.filesystems.FileUtil.createData(toDir, toFile);
                String content = readResource(loadResource(fromFile));
                if (content != null) {
                    FileLock lock = xml.lock();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(xml.getOutputStream(lock)));
                    try {
                        bw.write(content);
                    } finally {
                        bw.close();
                        lock.releaseLock();
                    }
                }
                result = xml;
            } catch (IOException e) {
                exception = e;
            }
        }

        private String readResource(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            String lineSep = System.getProperty("line.separator"); // NOI18N
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(lineSep);
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
            return sb.toString();
        }
    }

    public static String getSimpleFileName(String path) {
        return path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
    }

    public static String getSimpleFileNameWithExt(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    public static String getFileExt(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }
    
        public static String readResource(InputStream is, String encoding) throws IOException {
        // read the config from resource first
        StringBuilder sbuffer = new StringBuilder();
        String lineSep = System.getProperty("line.separator");//NOI18N
        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        String line = br.readLine();
        while (line != null) {
            sbuffer.append(line);
            sbuffer.append(lineSep);
            line = br.readLine();
        }
        br.close();
        return sbuffer.toString();
    }

    public static void createFile(FileObject target, String content, String encoding) throws IOException {
        FileLock lock = target.lock();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(target.getOutputStream(lock), encoding));
            bw.write(content);
            bw.close();

        } finally {
            lock.releaseLock();
        }
    }

}
