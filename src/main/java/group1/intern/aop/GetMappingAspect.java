package group1.intern.aop;

import group1.intern.util.util.WebUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class GetMappingAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getAction() {
    }

    @Before("getAction()")
    public void setUrlAction(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getTarget().getClass();

        try {
            String url = getRequestUrl(joinPoint, clazz);
            WebUtils.Sessions.setCurrentGetUrl(url);
        } catch (Exception ignored) {
        }
    }

    private String getRequestUrl(JoinPoint joinPoint, Class<?> clazz) {
        Map<String, Object> pathVariables = new HashMap<>();
        Map<String, Object> requestParams = new HashMap<>();
        Object[] args = joinPoint.getArgs();

        // Get method signature + method + parameter annotations
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        // Get path variables & request params
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof PathVariable pathVariable) {
                    String variableName = pathVariable.value();
                    pathVariables.put(variableName, args[i]);
                    continue;
                }
                if (annotation instanceof RequestParam requestParam) {
                    String paramName = requestParam.value();
                    requestParams.put(paramName, args[i]);
                }
            }
        }

        // Get RequestMapping & GetMapping annotation
        GetMapping methodGetMapping = method.getAnnotation(GetMapping.class);
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        return getGetUrl(requestMapping, methodGetMapping, pathVariables, requestParams);
    }

    private String getGetUrl(
        RequestMapping requestMapping,
        GetMapping getMapping,
        Map<String, Object> pathVariables,
        Map<String, Object> requestParams
    ) {
        String baseUrl = requestMapping != null ? getUrl(requestMapping.value()) : null;
        StringBuilder endpoint = new StringBuilder(getUrl(getMapping.value()));

        // Replace path variables
        for (Map.Entry<String, Object> entry : pathVariables.entrySet()) {
            if (entry.getValue() == null)
                throw new IllegalArgumentException("Path variable " + entry.getKey() + " is null");
            endpoint = new StringBuilder(endpoint.toString().replace("{" + entry.getKey() + "}", entry.getValue().toString()));
        }

        // Add request params
        boolean isFirstParam = true;
        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
            if (entry.getValue() != null) {
                endpoint.append(isFirstParam ? "?" : "&").append(entry.getKey()).append("=").append(entry.getValue());
                isFirstParam = false;
            }
        }
        return baseUrl != null ? baseUrl + endpoint : endpoint.toString();
    }

    private String getUrl(String[] urls) {
        return urls.length == 0 ? "" : urls[0];
    }
}
