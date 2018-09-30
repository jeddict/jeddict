/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.test.mock;

import java.util.Collections;
import java.util.Set;
import javax.swing.text.Document;
import org.netbeans.modules.parsing.impl.indexing.implspi.ActiveDocumentProvider;

/**
 *
 * @author jGauravGupta
 */
public class MockActiveDocumentProvider implements ActiveDocumentProvider {

    @Override
    public Document getActiveDocument() {
        return null;
    }

    @Override
    public Set<? extends Document> getActiveDocuments() {
        return Collections.emptySet();
    }

    @Override
    public void addActiveDocumentListener(ActiveDocumentProvider.ActiveDocumentListener listener) {
    }

    @Override
    public void removeActiveDocumentListener(ActiveDocumentProvider.ActiveDocumentListener listener) {
    }
}
