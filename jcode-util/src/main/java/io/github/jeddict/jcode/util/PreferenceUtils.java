/**
 * Copyright 2013-2019 the original author or authors from the Jeddict project (https://jeddict.github.io/).
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

import static io.github.jeddict.jcode.util.StringHelper.kebabCase;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyVisibilityStrategy;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.util.Exceptions;

public class PreferenceUtils {

    private final static Jsonb JSONB = JsonbBuilder.create(
            new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
                @Override
                public boolean isVisible(Field field) {
                    return true;
                }

                @Override
                public boolean isVisible(Method method) {
                    return false;
                }
            })
    );

    private static void serialize(Serializable obj, OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException("The OutputStream must not be null");
        }
        ObjectOutputStream out = null;
        try {
            // stream closed in the finally
            out = new ObjectOutputStream(outputStream);
            out.writeObject(obj);

        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    private static byte[] serialize(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }

    private static Object deserialize(InputStream inputStream, ClassLoader classLoader) throws InvalidClassException {
        if (inputStream == null) {
            throw new IllegalArgumentException("The InputStream must not be null");
        }
        ObjectInputStream in = null;
        try {
            // stream closed in the finally
            in = new ClassLoaderObjectInputStream(classLoader, inputStream);
            return in.readObject();

        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        } catch (InvalidClassException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    private static Object deserialize(byte[] objectData, ClassLoader classLoader) throws InvalidClassException {
        if (objectData == null) {
            throw new IllegalArgumentException("The byte[] must not be null");
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        return deserialize(bais, classLoader);
    }

    public static <T> T get(Project project, Class<T> _class) {
        Preferences pref = ProjectUtils.getPreferences(project, _class, true);
        return get(pref, _class);
    }

    public static <T> T get(Preferences pref, Class<T> _class) {
        T newInstance = null;
        try {
            newInstance = _class.newInstance();
            return (T) JSONB.fromJson(
                    pref.get(
                            kebabCase(_class.getSimpleName()),
                              JSONB.toJson(newInstance)
                    ),
                    _class
            );
        } catch(JsonbException ex){
            Exceptions.printStackTrace(ex);
            return newInstance;
        } catch (InstantiationException | IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public static <T> void set(Project project, T t) {
        Preferences pref = ProjectUtils.getPreferences(project, t.getClass(), true);
        set(pref, t);
    }

    public static <T> void set(Preferences pref, T t) {
          pref.put(kebabCase(t.getClass().getSimpleName()), JSONB.toJson(t));
    }

}

class ClassLoaderObjectInputStream extends ObjectInputStream {

    private final ClassLoader classLoader;

    public ClassLoaderObjectInputStream(ClassLoader classLoader, InputStream in) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {

        try {
            String name = desc.getName();
            return Class.forName(name, false, classLoader);
        } catch (ClassNotFoundException e) {
            return super.resolveClass(desc);
        }
    }
}
