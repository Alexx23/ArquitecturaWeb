package web.practicafinal.utils;

import java.lang.reflect.Field;
import web.practicafinal.models.controllers.ModelController;

/**
 *
 * @author Alex
 */
public class InstanceConverter {
    
    public static void updateInstance(Class<?> targetClass, Object targetObject, Class<?> sourceClass, Object sourceObject) {
        // Obtener los campos de la clase fuente
        Field[] sourceFields = sourceClass.getDeclaredFields();

        // Iterar sobre los campos de la clase fuente
        for (Field sourceField : sourceFields) {
            sourceField.setAccessible(true);

            try {
                // Obtener el valor y el tipo del campo en la instancia de la clase fuente
                Object value = sourceField.get(sourceObject);
                Class<?> sourceFieldType = sourceField.getType();
                
                // Verificar si el valor no es nulo
                if (value == null) continue;

                // Verificar si los tipos son compatibles
                if (isCompatibleType(targetClass.getDeclaredField(sourceField.getName()).getType(), sourceFieldType)) {
                    // Actualizar el valor correspondiente en la instancia de la clase objetivo
                    Field targetField = targetClass.getDeclaredField(sourceField.getName());
                    targetField.setAccessible(true);
                    targetField.set(targetObject, value);
                } else {
                    // Si los tipos NO son compatibles
                    // Actualizar el valor correspondiente compatible en la instancia de la clase objetivo
                    Object valueCompatible = getCompatibleType(sourceField.getName(), value);
                    if (valueCompatible == null) continue;
                    Field targetField = targetClass.getDeclaredField(sourceField.getName());
                    targetField.setAccessible(true);
                    targetField.set(targetObject, valueCompatible);
                    
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static boolean isCompatibleType(Class<?> target, Class<?> source) {
        // Verificar compatibilidad teniendo en cuenta tipos primitivos y sus equivalentes de clase
        return target.isAssignableFrom(source) || (target.isPrimitive() && getWrapperClass(target).equals(source));
    }
    
    private static Class<?> getWrapperClass(Class<?> primitiveType) {
        // Obtener la clase envolvente de un tipo primitivo
        if (primitiveType == int.class) {
            return Integer.class;
        } else if (primitiveType == short.class) {
            return Short.class;
        } else {
            // Añadir aquí más conversiones según sea necesario
            return primitiveType;
        }
    }  
    
    
    private static Object getCompatibleType(String fieldName, Object fieldValue) {
        switch (fieldName) {
            case "ageClassificationId":
                return ModelController.getAgeClassification().findAgeClassification((Integer)fieldValue);
            case "diretorId":
                return ModelController.getDirector().findDirector((Integer)fieldValue);
            case "distributorId":
                return ModelController.getDistributor().findDistributor((Integer)fieldValue);
            case "genreId":
                return ModelController.getGenre().findGenre((Integer)fieldValue);
            case "nationalityId":
                return ModelController.getNationality().findNationality((Integer)fieldValue);
            case "movieId":
                return ModelController.getMovie().findMovie((Integer)fieldValue);
            case "roomId":
                return ModelController.getRoom().findRoom((Integer)fieldValue);
            // Añadir aquí más conversiones según sea necesario
        }
        return null;
    }
  
}
