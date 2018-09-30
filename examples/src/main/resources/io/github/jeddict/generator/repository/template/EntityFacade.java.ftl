<#if package!="">package ${package};</#if>

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ${EntityClass_FQN};

@Stateless
@Named("${entityInstance}")
public class ${EntityFacade} extends ${AbstractFacade}<${EntityClass}> {

    @PersistenceContext(unitName = "${PU}")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ${EntityFacade}() {
        super(${EntityClass}.class);
    }
    
}
