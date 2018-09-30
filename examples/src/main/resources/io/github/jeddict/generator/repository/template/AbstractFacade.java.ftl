<#if package!="">package ${package};</#if>

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class ${AbstractFacade}<T> {

    private final Class<T> entityClass;

    public ${AbstractFacade}(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public T edit(T entity) {
        return getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int startPosition, int size) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(size);
        q.setFirstResult(startPosition);
        return q.getResultList();
    }

    public int count() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public Optional<T> findSingleByNamedQuery(String namedQueryName, Class<T> classT) {
        return findOrEmpty(() -> getEntityManager().createNamedQuery(namedQueryName, classT).getSingleResult());
    }

    public Optional<T> findSingleByNamedQuery(String namedQueryName, Map<String, Object> parameters, Class<T> classT) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        TypedQuery<T> query = getEntityManager().createNamedQuery(namedQueryName, classT);
        rawParameters.stream().forEach((entry) -> {
            query.setParameter(entry.getKey(), entry.getValue());
        });
        return findOrEmpty(() -> query.getSingleResult());
    }

    public List<T> findByNamedQuery(String namedQueryName) {
        return getEntityManager().createNamedQuery(namedQueryName).getResultList();
    }

    public List<T> findByNamedQuery(String namedQueryName, Map<String, Object> parameters) {
        return findByNamedQuery(namedQueryName, parameters, 0);
    }

    public List<T> findByNamedQuery(String queryName, int resultLimit) {
        return getEntityManager().createNamedQuery(queryName).
                setMaxResults(resultLimit).getResultList();
    }

    public List<T> findByNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        Query query = getEntityManager().createNamedQuery(namedQueryName);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        rawParameters.stream().forEach((entry) -> {
            query.setParameter(entry.getKey(), entry.getValue());
        });
        return query.getResultList();
    }

    public static <T> Optional<T> findOrEmpty(final DaoRetriever<T> retriever) {
        try {
            return Optional.of(retriever.retrieve());
        } catch (NoResultException ex) {
            //log
        }
        return Optional.empty();
    }

    @FunctionalInterface
    public interface DaoRetriever<T> {

        T retrieve() throws NoResultException;
    }

}
