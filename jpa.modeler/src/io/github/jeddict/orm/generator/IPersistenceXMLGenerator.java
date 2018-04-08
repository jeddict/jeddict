/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.jeddict.orm.generator;

import io.github.jeddict.jcode.task.ITaskSupervisor;
import io.github.jeddict.jpa.spec.EntityMappings;
import java.util.List;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;

/**
 *
 * @author gaura
 */
public interface IPersistenceXMLGenerator {
    
    void generatePersistenceXML(ITaskSupervisor task, Project project, SourceGroup sourceGroup, EntityMappings entityMappings, List<String> classNames);
    
}
