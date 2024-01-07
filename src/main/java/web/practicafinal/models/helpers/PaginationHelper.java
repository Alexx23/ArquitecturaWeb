/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package web.practicafinal.models.helpers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;
import web.practicafinal.models.controllers.ModelController;
import web.practicafinal.utils.Response;

/**
 *
 * @author Alex
 */
public class PaginationHelper {
    
    public static <T> DataListContainer getPaginated(Class<T> entityClass, int actualPage, String nameValue) {
        int pageSize = 20;

        List<T> data = getEntitiesPagination(entityClass, actualPage, pageSize, nameValue);
        long totalSize = getTotalCount(entityClass, nameValue);
        
        return new DataListContainer(data, actualPage, pageSize, totalSize);
    }
    
    private static EntityManager em = ModelController.getEMF().createEntityManager();
    
    private static <T> List<T> getEntitiesPagination(Class<T> entityClass, int actualPage, int pageSize, String nameValue) {
        TypedQuery<T> query = null;
        if (nameValue != null) {
            query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e.name LIKE :name", entityClass);
            query.setParameter("name", "%"+nameValue+"%");
        } else {
            query = em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass);
        }
        
        query.setFirstResult((actualPage - 1) * pageSize);
        query.setMaxResults(pageSize);
        List<T> resultList = query.getResultList();
        return resultList;
    }

    private static <T> long getTotalCount(Class<T> entityClass, String nameValue) {
        Query queryTotal = null;
        if (nameValue != null) {
            queryTotal = em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e.name LIKE :name", entityClass);
            queryTotal.setParameter("name", "%"+nameValue+"%");
        } else {
            queryTotal = em.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e");
        }
        
        return (long) queryTotal.getSingleResult();
    }
    
    private static class DataListContainer {
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
