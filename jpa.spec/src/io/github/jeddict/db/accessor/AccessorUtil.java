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
package io.github.jeddict.db.accessor;

import java.sql.Blob;
import java.sql.Clob;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENUM_ORDINAL;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ENUM_STRING;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.DirectAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.converters.EnumeratedMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.LobMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import io.github.jeddict.jcode.util.AttributeType;
import static io.github.jeddict.jcode.util.AttributeType.BYTE;
import static io.github.jeddict.jcode.util.AttributeType.BYTE_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.CHAR;
import static io.github.jeddict.jcode.util.AttributeType.CHAR_WRAPPER;
import static io.github.jeddict.jcode.util.AttributeType.isArray;
import io.github.jeddict.db.accessor.spec.MapKeyAccessor;
import io.github.jeddict.jpa.spec.EnumType;
import io.github.jeddict.jpa.spec.Lob;
import io.github.jeddict.jpa.spec.TemporalType;

/**
 *
 * @author Gaurav Gupta
 */
public class AccessorUtil {

    public static void setEnumerated(MapKeyAccessor accessor, EnumType enumType) {
        if (enumType == null) {
            return;
        }
        EnumeratedMetadata enumeratedMetadata = new EnumeratedMetadata();
        if (enumType == EnumType.STRING) {
            enumeratedMetadata.setEnumeratedType(JPA_ENUM_STRING);
        } else {
            enumeratedMetadata.setEnumeratedType(JPA_ENUM_ORDINAL);
        }
        accessor.setMapKeyEnumerated(enumeratedMetadata);
        accessor.setMapKeyClassName(ProxyEnum.class.getName());
        // orignal enum is not accessible in classloader so either to use proxy enum or create dynamic enum
    }
    
    public static void setEnumerated(DirectAccessor accessor, EnumType enumType) {
        if (enumType == null) {
            return;
        }
        EnumeratedMetadata enumeratedMetadata = new EnumeratedMetadata();
        if (enumType == EnumType.STRING) {
            enumeratedMetadata.setEnumeratedType(JPA_ENUM_STRING);
        } else {
            enumeratedMetadata.setEnumeratedType(JPA_ENUM_ORDINAL);
        }
        if (accessor instanceof ElementCollectionAccessor) {
            ((ElementCollectionAccessor) accessor).setTargetClassName(ProxyEnum.class.getName());
            accessor.setEnumerated(enumeratedMetadata);
        } else {
            accessor.setAttributeType(ProxyEnum.class.getName()); 
            accessor.setEnumerated(enumeratedMetadata);
        }
    }

    public static void setTemporal(MapKeyAccessor accessor, TemporalType temporalType) {
        if (temporalType == null) {
            return;
        }
        TemporalMetadata temporalMetadata = new TemporalMetadata();
        temporalMetadata.setTemporalType(temporalType.toString());
        accessor.setMapKeyTemporal(temporalMetadata);
    }
    
     public static void setTemporal(DirectAccessor accessor, TemporalType temporalType) {
        if (temporalType == null) {
            return;
        }
        TemporalMetadata temporalMetadata = new TemporalMetadata();
        temporalMetadata.setTemporalType(temporalType.toString());
        accessor.setTemporal(temporalMetadata);// JPA_TEMPORAL_DATE = "DATE"; JPA_TEMPORAL_TIME = "TIME"; JPA_TEMPORAL_TIMESTAMP = "TIMESTAMP";
    }

    public static void setLob(DirectAccessor accessor, Lob lob, String attributeType, boolean isCollectionType) {
        if (lob == null || attributeType == null) {
            return;
        }
        if (isArray(attributeType)) {
            String attributeArrayType = AttributeType.getArrayType(attributeType);
            if (attributeArrayType.equals(BYTE) || attributeArrayType.equals(BYTE_WRAPPER)) { //https://github.com/jeddict/jeddict/issues/5 , https://github.com/jeddict/jeddict/issues/6
                if (isCollectionType) {
                    ((ElementCollectionAccessor) accessor).setTargetClassName(Blob.class.getName());
                } else {
                    accessor.setAttributeType(Blob.class.getName());
                }
            } else if (attributeArrayType.equals(CHAR) || attributeArrayType.equals(CHAR_WRAPPER)) {
                if (isCollectionType) {
                    ((ElementCollectionAccessor) accessor).setTargetClassName(Clob.class.getName());
                } else {
                    accessor.setAttributeType(Clob.class.getName());
                }
            }
        }

        accessor.setLob(new LobMetadata());

    }

    public static enum ProxyEnum {

        DEFAULT;
    }

}
