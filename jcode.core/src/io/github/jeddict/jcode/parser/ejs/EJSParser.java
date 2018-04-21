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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.io.IOUtils;
import org.openide.util.Exceptions;

public final class EJSParser {

    private ScriptEngine engine;
    private final List<Map<String, Object>> contexts = new ArrayList<>();
    private final StringBuilder scripts = new StringBuilder();
    private Character delimiter;
    private Map<String, String> importTemplate;
    private static final Set<String> SKIP_FILE_TYPE = new HashSet<>(
            Arrays.asList("png", "jpeg", "jpg", "gif"));

    private static String base;
    private static String ejs;
    
    private ScriptEngine createEngine() {
        CompiledScript cscript;
        Bindings bindings;
        ScriptEngine scriptEngine = new NashornScriptEngineFactory().getScriptEngine("--language=es6");//engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            try {
                if (base == null) {
                    base = IOUtils.toString(EJSParser.class.getClassLoader().getResource("io/github/jeddict/jcode/parser/ejs/resources/base.js"), "UTF-8");
                }
                if (ejs == null) {
                    ejs = IOUtils.toString(EJSParser.class.getClassLoader().getResource("io/github/jeddict/jcode/parser/ejs/resources/ejs.js"), "UTF-8");
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
     
            scriptEngine.eval(base);
            Compilable compilingEngine = (Compilable) scriptEngine;
            cscript = compilingEngine.compile(ejs);
            bindings = scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            cscript.eval(bindings);
            scriptEngine.eval(scripts.toString());

            for (Map<String, Object> context : contexts) {
                context.keySet()
                        .stream()
                        .forEach((key) -> {
                            try {
                                bindings.put(key, context.get(key));
                                if (context.get(key) instanceof Collection) {
                                    scriptEngine.eval(String.format("%s = Java.from(%s);", key, key));
                                }
                            } catch (ScriptException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        });
            }

        } catch (ScriptException ex) {
            Exceptions.printStackTrace(ex);
        }
        return scriptEngine;
    }

    public void addContext(Map<String, Object> context) {
        contexts.add(context);
    }

    public void addContext(Object context) {
        if (context != null) {
            try {
                addContext(introspect(context));
            } catch (Exception ex) {
                Logger.getLogger(EJSParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Map<String, Object> introspect(Object obj) {
        ObjectMapper m = new ObjectMapper();
        Map<String, Object> mappedObject = m.convertValue(obj, Map.class);
        return mappedObject;
    }

    public String parse(String template) throws ScriptException {
        String result = null;
        try {
            if (engine == null) {
                engine = createEngine();
            }
            Object ejs = engine.eval("ejs");
            Invocable invocable = (Invocable) engine;
            Map<String, Object> options = new HashMap<>();
            options.put("filename", "template");
            if (importTemplate != null) {
                options.put("ext", importTemplate);
            }
            if (delimiter != null) {
                options.put("delimiter", delimiter);
            }

            result = (String) invocable.invokeMethod(ejs, "render", template, null, options);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }

    public String parse(Reader reader) throws ScriptException, IOException {
        String parsed;
        try (StringWriter writer = new StringWriter()) {
            IOUtils.copy(reader, writer);
            parsed = parse(writer.toString());
        }
        return parsed;
    }


    public void eval(String script) {
        scripts.append(script);
    }

    /**
     * @return the delimiter
     */
    public Character getDelimiter() {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(Character delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @return the importTemplate
     */
    public Map<String, String> getImportTemplate() {
        return importTemplate;
    }

    /**
     * @param importTemplate the importTemplate to set
     */
    public void setImportTemplate(Map<String, String> importTemplate) {
        this.importTemplate = importTemplate;
    }

//    private static final Set<String> PARSER_FILE_TYPE = new HashSet<>(Arrays.asList(
//            "html", "js", "css", "scss", "json", "properties", "ts", "ejs", "txt", "webapp", "yml", "sh"));

    public static boolean isTextFile(String file) {
        return true;
    }

    public Consumer<FileTypeStream> getParserManager() {
        return getParserManager(null);
    }

    public Consumer<FileTypeStream> getParserManager(List<String> skipFile) {
        return (fileType) -> {
            try {
                if (SKIP_FILE_TYPE.contains(fileType.getFileType())
                        || (skipFile != null && skipFile.contains(fileType.getFileName()))
                        || fileType.isSkipParsing()) {
                    IOUtils.copy(fileType.getInputStream(), fileType.getOutputStream());
                    if (!(fileType.getInputStream() instanceof ZipInputStream)) {
                        fileType.getInputStream().close();
                    }
                    fileType.getOutputStream().close();
                } else {
                    Charset charset = Charset.forName("UTF-8");
                    Reader reader = new BufferedReader(new InputStreamReader(fileType.getInputStream(), charset));
                    Writer writer = new BufferedWriter(new OutputStreamWriter(fileType.getOutputStream(), charset));
                    IOUtils.write(parse(reader), writer);
                    if (!(fileType.getInputStream() instanceof ZipInputStream)) {
                        reader.close();
                    }
                    writer.flush();
                    writer.close();
                }

            } catch (ScriptException | IOException ex) {
                Exceptions.printStackTrace(ex);
                System.out.println("Error in template : " + fileType.getFileName());
            }
        };
    }
}
