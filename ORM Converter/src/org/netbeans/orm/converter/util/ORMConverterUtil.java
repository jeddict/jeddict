/**
 * Copyright [2014] Gaurav Gupta
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
package org.netbeans.orm.converter.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.netbeans.orm.converter.compiler.InvalidDataException;
import org.netbeans.orm.converter.compiler.WritableSnippet;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;

public class ORMConverterUtil {

    public static final String AT = "@";
    public static final String CLASS_SUFFIX = ".class";
    public static final String CLOSE_BRACES = "}";
    public static final String CLOSE_PARANTHESES = ")";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String GREATER_THAN = ">";
    public static final String IMPORT = "import ";
    public static final String LESS_THAN = "<";
    public static final String NEW_LINE = "\n";
    public static final String NEW_TAB = "\t";
    public static final String HALF_TAB = "  ";
    public static final String TAB = "    ";
    public static final String OPEN_BRACES = "{";
    public static final String OPEN_PARANTHESES = "(";
    public static final String QUESTION = "?";
    public static final String SINGLE_QUOTE = "'";
    public static final String QUOTE = "\"";
    public static final String SEMICOLON = ";";
    public static final String SOURCE_SUFFIX = ".java";
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String EQUAL = "=";

    public static File createFile(String parentDir, String childDir,
            String fileName) throws IOException {

        if (childDir == null) {
            return createFile(parentDir, fileName);
        }

        File dir = new File(parentDir, childDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);
        if (!file.exists()) {
            file.delete();
        }
        file.createNewFile();

        return file;
    }

    public static File createFile(String parentDir, String fileName)
            throws IOException {

        File dir = new File(parentDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);

        if (!file.exists()) {
            file.createNewFile();
        }

        return file;
    }

    public static Collection<String> eliminateSamePkgImports(String classPackage, Collection<String> importSnippets) {

        List<String> uniqueImports = new ArrayList<>();

        for (String importSnippet : importSnippets) {

            ClassHelper importSnippetHelper = new ClassHelper(importSnippet);

            if (importSnippetHelper.getPackageName() == null) {
                continue;
            }

            if (!importSnippetHelper.getPackageName().equals(classPackage)) {
                uniqueImports.add(importSnippet);
            }
        }

        return uniqueImports;
    }

    public static byte[] getBytes(File file) throws IOException {

        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        if (file.length() > Integer.MAX_VALUE) {
            throw new IOException("file is too large for single read");
        }

        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = null;

        try {

            fileInputStream = new FileInputStream(file);
            int read = fileInputStream.read(bytes);

            if (read != file.length()) {
                throw new IOException("could not read entire file");
            }

        } finally {
            fileInputStream.close();
        }

        return bytes;
    }

    public static String getCommaSeparatedString(Collection<String> values) {

        if (values == null || values.size() == 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        for (String value : values) {
            builder.append(value);
            builder.append(COMMA);
        }

        return builder.substring(0, builder.length() - 1);
    }

    public static String getContent(File file) throws IOException {
        return new String(getBytes(file));
    }

    public static String getContent(File file, final String charsetName)
            throws IOException {

        return new String(getBytes(file), charsetName);
    }

    static {
        try {
            Properties properties = new Properties();
            properties.load(ORMConverterUtil.class.getClassLoader().getResourceAsStream("velocity.properties"));
            Velocity.init(properties);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static String writeToTemplate(String templateName, Map context) throws Exception {
        Template template = Velocity.getTemplate(templateName);
        ByteArrayOutputStream generatedClass = new ByteArrayOutputStream();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(generatedClass, "UTF-8"))) {
            if (template != null) {
                template.merge(new VelocityContext(context), writer);
            }
            writer.flush();
        }
        return generatedClass.toString();
    }

    public static Collection<String> processedImportStatements(
            Collection<String> importSnippets) {

        Collection<String> processedStatements = new ArrayList<>();
        for (String element : importSnippets) {
            processedStatements.add(IMPORT + element + SEMICOLON);
        }

        return processedStatements;
    }

    public static void writeContent(String content, File file)
            throws IOException {

        writeContent(content, Charset.defaultCharset().toString(), file);
    }

    public static void writeContent(String content, String charset, File file)
            throws IOException {

        if (file.isDirectory()) {
            throw new IOException("Cannot write content to directory");
        }

        if (file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content.getBytes(charset));
        fos.close();
    }

    public static FileObject writeSnippet(WritableSnippet writableSnippet, File destDir)
            throws InvalidDataException, IOException {

        File sourceFile = ORMConverterUtil.createFile(
                destDir.getAbsolutePath(),
                writableSnippet.getClassHelper().getSourcePath(),
                writableSnippet.getClassHelper().getClassNameWithSourceSuffix());
        final FileObject fo = FileUtil.toFileObject(sourceFile);
       
        try {
            String content = writableSnippet.getSnippet();
            ORMConverterUtil.writeContent(getFormattedText(content, "java"), sourceFile);
        } catch (InvalidDataException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return fo;
    }

    public static String getFormattedText(String textToFormat, String ext) {
        try {
            FileSystem fs = FileUtil.createMemoryFileSystem();
            FileObject root = fs.getRoot();
            String fileName = FileUtil.findFreeFileName(root, "sample-format", ext);// NOI18N
            FileObject data = FileUtil.createData(root, fileName + "." + ext);// NOI18N
            Writer writer = new OutputStreamWriter(data.getOutputStream(), "UTF8");// NOI18N
            try {
                writer.append(textToFormat);
                writer.flush();
            } finally {
                writer.close();
            }
            DataObject dob = DataObject.find(data);
            EditorCookie ec = dob.getLookup().lookup(EditorCookie.class);
            if (ec != null) {
                final StyledDocument fmtDoc = ec.openDocument();
                final Reformat fmt = Reformat.get(fmtDoc);
                fmt.lock();
                try {
                    final Runnable runnable = () -> {
                        try {
                            fmt.reformat(0, fmtDoc.getLength());
                        } catch (BadLocationException ex) {
                        }
                    };
                    if (fmtDoc instanceof BaseDocument) {
                        ((BaseDocument) fmtDoc).runAtomic(runnable);
                    } else {
                        runnable.run();
                    }
                } finally {
                    fmt.unlock();
                }
                SaveCookie save = dob.getLookup().lookup(SaveCookie.class);
                if (save != null) {
                    save.save();
                }
                final String text = fmtDoc.getText(0, fmtDoc.getLength());
                StringBuilder declText = new StringBuilder();
                final int len = text.length();
                int start = 0;
                int end = len - 1;
                // skip all whitespaces in the beginning and end of formatted text
                for (; start < len && Character.isWhitespace(text.charAt(start)); start++) {
                }
                for (; end > start && Character.isWhitespace(text.charAt(end)); end--) {
                }
                
                for (int i = start; i <= end; i++) {
                    final char charAt = text.charAt(i);
                    if (charAt == '\n') { 
                        if (i <= end) {
                            declText.append(charAt);
                        }
                    } else {
                        declText.append(charAt);
                    }
                }
                return declText.toString();
            }
            data.delete();
        } catch (BadLocationException | IOException ex) {
            ex.printStackTrace(System.err);
        }
        return textToFormat;
    }

}
