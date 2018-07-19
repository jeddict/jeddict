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

package io.github.jeddict.jpa.spec.extend.adapter;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OrderColumnAdapter extends XmlAdapter<AdaptedMap, Map<String, String>> {

    @Override
    public AdaptedMap marshal(Map<String, String> map) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        Element rootElement = document.createElement("map");
        document.appendChild(rootElement);

        for(Entry<String,String> entry : map.entrySet()) {
            Element mapElement = document.createElement(entry.getKey());
            mapElement.setTextContent(entry.getValue());
            rootElement.appendChild(mapElement);
        }

        AdaptedMap adaptedMap = new AdaptedMap();
        adaptedMap.setValue(document);
        return adaptedMap;
    }

    @Override
    public Map<String, String> unmarshal(AdaptedMap adaptedMap) throws Exception {
        Map<String, String> map = new HashMap<>();
        Element rootElement = (Element) adaptedMap.getValue();
        NodeList childNodes = rootElement.getChildNodes();
        for(int x=0,size=childNodes.getLength(); x<size; x++) {
            Node childNode = childNodes.item(x);
            if(childNode.getNodeType() == Node.ELEMENT_NODE) {
                map.put(childNode.getLocalName(), childNode.getTextContent());
            }
        }
        return map;
    }

}


 