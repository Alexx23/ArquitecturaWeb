package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class PaginationHelper {
    
    public EntityManager getEntityManager() {
        return ModelController.getEMF().createEntityManager();
    }
    
    public static <T> DataListContainer getPaginated(Class<T> entityClass, int actualPage, String nameValue) {
        int pageSize = 20;
        
        PaginationHelper paginationHelper = new PaginationHelper();
        
        EntityManager em = paginationHelper.getEntityManager();

        List<T> data = getEntitiesPagination(em, entityClass, actualPage, pageSize, nameValue);
        long totalSize = getTotalCount(em, entityClass, nameValue);
        
        return new DataListContainer(data, actualPage, pageSize, totalSize);
    }
    
    public static <T> DataListContainer getPaginatedWithQuery(Class<T> entityClass, int actualPage, TypedQuery<T> query, Query queryTotalCount) {
        int pageSize = 20;
        
        PaginationHelper paginationHelper = new PaginationHelper();
        
        EntityManager em = paginationHelper.getEntityManager();

        List<T> data = getEntitiesPagination(em, entityClass, actualPage, pageSize, query);
        long totalSize = getTotalCount(queryTotalCount);
        
        return new DataListContainer(data, actualPage, pageSize, totalSize);
    }
    
    public static <T> DataListContainer getPaginatedWithFilters(Class<T> entityClass, int actualPage, Map<String, Object> filters) {
        int pageSize = 20;
        
        PaginationHelper paginationHelper = new PaginationHelper();
        
        EntityManager em = paginationHelper.getEntityManager();

        List<T> data = getEntitiesPagination(em, entityClass, actualPage, pageSize, filters);
        long totalSize = getTotalCount(em, entityClass, filters);
        
        return new DataListContainer(data, actualPage, pageSize, totalSize);
    }
    
    
    //  PAGINATIONS
    /////////////////
    
    private static <T> List<T> getEntitiesPagination(EntityManager em, Class<T> entityClass, int actualPage, int pageSize, String nameValue) {
        TypedQuery<T> query = null;
        if (nameValue != null) {
            query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.name LIKE :name ORDER BY e.id DESC", entityClass);
            query.setParameter("name", "%"+nameValue+"%");
        } else {
            query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e ORDER BY e.id DESC", entityClass);
        }
        
        query.setFirstResult((actualPage - 1) * pageSize);
        query.setMaxResults(pageSize);
        List<T> resultList = query.getResultList();
        return resultList;
    }
    
    private static <T> List<T> getEntitiesPagination(EntityManager em, Class<T> entityClass, int actualPage, int pageSize, TypedQuery<T> query) {
        query.setFirstResult((actualPage - 1) * pageSize);
        query.setMaxResults(pageSize);
        List<T> resultList = query.getResultList();
        return resultList;
    }
    
    private static <T> List<T> getEntitiesPagination(EntityManager em, Class<T> entityClass, int actualPage, int pageSize, Map<String, Object> filters) {
        StringBuilder jpql = new StringBuilder("SELECT e FROM " + entityClass.getSimpleName() + " e");
        
        if (filters != null && !filters.isEmpty()) {
            jpql.append(" WHERE");
            for (String key : filters.keySet()) {
                jpql.append(" e.").append(key).append(" = :").append(key).append(" AND");
            }
            jpql.delete(jpql.length() - 4, jpql.length()); // Elimina el " AND"
        }

        jpql.append(" ORDER BY e.id DESC");
        
        TypedQuery<T> query = em.createQuery(jpql.toString(), entityClass);

        if (filters != null) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }

        query.setFirstResult((actualPage - 1) * pageSize);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }
    
    //  TOTAL COUNT
    /////////////////

    private static <T> long getTotalCount(EntityManager em, Class<T> entityClass, String nameValue) {
        Query queryTotal = null;
        if (nameValue != null) {
            queryTotal = em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e.name LIKE :name", entityClass);
            queryTotal.setParameter("name", "%"+nameValue+"%");
        } else {
            queryTotal = em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e");
        }
        
        return (long) queryTotal.getSingleResult();
    }
    
    private static <T> long getTotalCount(Query queryTotal) {
        return (long) queryTotal.getSingleResult();
    }
    
    private static <T> long getTotalCount(EntityManager em, Class<T> entityClass, Map<String, Object> filters) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e");

        if (filters != null && !filters.isEmpty()) {
            jpql.append(" WHERE");
            for (String key : filters.keySet()) {
                jpql.append(" e.").append(key).append(" = :").append(key).append(" AND");
            }
            jpql.delete(jpql.length() - 4, jpql.length()); // Elimina el " AND"
        }

        Query queryTotal = em.createQuery(jpql.toString(), entityClass);

        if (filters != null) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                queryTotal.setParameter(entry.getKey(), entry.getValue());
            }
        }

        return (long) queryTotal.getSingleResult();
    }
    
    private DataListContainer createDataListContainer(Object data, int actualPage, int pageSize, long totalSize) {
        return new DataListContainer(data, actualPage, pageSize, totalSize);
    }
    
    public static class DataListContainer {
        private Object data;
        private int actualPage;
        private int pageSize;
        private long totalSize;
        private boolean hasMore;
        
        
        public DataListContainer(Object data, int actualPage, int pageSize, long totalSize) {
            this.data = data;
            this.actualPage = actualPage;
            this.pageSize = pageSize;
            this.totalSize = totalSize;
            this.hasMore = (actualPage * pageSize) < totalSize;
        }

        public Object getData() {
            return data;
        }
        
        public Object getActualPage() {
            return actualPage;
        }
        
        public int getPageSize() {
            return pageSize;
        }
        
        public long getTotalSize() {
            return totalSize;
        }
        
        public boolean getHasMore() {
            return hasMore;
        }
    }
    
}
