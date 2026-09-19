package com.PrepTrack_AI.Fullstack_Project.audit.aspect;

import com.PrepTrack_AI.Fullstack_Project.audit.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    @Autowired
    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    // ── Pointcuts ─────────────────────────────────────────────────────────────

    @Pointcut("execution(* com.PrepTrack_AI.Fullstack_Project.service.AuthService.register(..))")
    public void registerPointcut() {}

    @Pointcut("execution(* com.PrepTrack_AI.Fullstack_Project.service.AuthService.login(..))")
    public void loginPointcut() {}

    @Pointcut("execution(* com.PrepTrack_AI.Fullstack_Project.service.UserService.updateUserProfile(..))")
    public void updateProfilePointcut() {}

    @Pointcut("execution(* com.PrepTrack_AI.Fullstack_Project.service.RoleService.createRole(..)) || " +
              "execution(* com.PrepTrack_AI.Fullstack_Project.service.RoleService.updateRole(..)) || " +
              "execution(* com.PrepTrack_AI.Fullstack_Project.service.RoleService.deleteRole(..))")
    public void rolePointcut() {}

    @Pointcut("execution(* com.PrepTrack_AI.Fullstack_Project.service.PermissionService.createPermission(..)) || " +
              "execution(* com.PrepTrack_AI.Fullstack_Project.service.PermissionService.deletePermission(..))")
    public void permissionPointcut() {}

    @Pointcut("registerPointcut() || loginPointcut() || updateProfilePointcut() || rolePointcut() || permissionPointcut()")
    public void auditActionsPointcut() {}

    // ── Success Advices ───────────────────────────────────────────────────────

    @AfterReturning(pointcut = "auditActionsPointcut()", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String performedBy = resolveUser(joinPoint);
        String ipAddress = getClientIp();

        String action = "UNKNOWN";
        String entityName = "UNKNOWN";
        String description = "Operation succeeded";
        Long entityId = extractId(result);

        if ("register".equals(methodName)) {
            action = "CREATE_USER";
            entityName = "User";
            description = "User registered successfully";
        } else if ("login".equals(methodName)) {
            action = "LOGIN_SUCCESS";
            entityName = "User";
            description = "User logged in successfully";
        } else if ("updateUserProfile".equals(methodName)) {
            action = "UPDATE_PROFILE";
            entityName = "User";
            description = "Updated user profile";
        } else if ("createRole".equals(methodName)) {
            action = "CREATE_ROLE";
            entityName = "Role";
            description = "Created role";
        } else if ("updateRole".equals(methodName)) {
            action = "UPDATE_ROLE";
            entityName = "Role";
            description = "Updated role mappings";
        } else if ("deleteRole".equals(methodName)) {
            action = "DELETE_ROLE";
            entityName = "Role";
            description = "Deleted role";
            entityId = extractIdFromArgs(joinPoint);
        } else if ("createPermission".equals(methodName)) {
            action = "CREATE_PERMISSION";
            entityName = "Permission";
            description = "Created permission";
        } else if ("deletePermission".equals(methodName)) {
            action = "DELETE_PERMISSION";
            entityName = "Permission";
            description = "Deleted permission";
            entityId = extractIdFromArgs(joinPoint);
        }

        auditService.logAction(action, performedBy, entityName, entityId, description, ipAddress, "SUCCESS");
    }

    // ── Failure Advices ───────────────────────────────────────────────────────

    @AfterThrowing(pointcut = "auditActionsPointcut()", throwing = "ex")
    public void logFailure(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        String performedBy = resolveUser(joinPoint);
        String ipAddress = getClientIp();

        String action = "UNKNOWN";
        String entityName = "UNKNOWN";
        String description = "Operation failed: " + ex.getMessage();
        Long entityId = extractIdFromArgs(joinPoint);

        if ("register".equals(methodName)) {
            action = "CREATE_USER";
            entityName = "User";
        } else if ("login".equals(methodName)) {
            action = "LOGIN_FAILURE";
            entityName = "User";
            description = "Failed login attempt: " + ex.getMessage();
        } else if ("updateUserProfile".equals(methodName)) {
            action = "UPDATE_PROFILE";
            entityName = "User";
        } else if ("createRole".equals(methodName)) {
            action = "CREATE_ROLE";
            entityName = "Role";
        } else if ("updateRole".equals(methodName)) {
            action = "UPDATE_ROLE";
            entityName = "Role";
        } else if ("deleteRole".equals(methodName)) {
            action = "DELETE_ROLE";
            entityName = "Role";
        } else if ("createPermission".equals(methodName)) {
            action = "CREATE_PERMISSION";
            entityName = "Permission";
        } else if ("deletePermission".equals(methodName)) {
            action = "DELETE_PERMISSION";
            entityName = "Permission";
        }

        auditService.logAction(action, performedBy, entityName, entityId, description, ipAddress, "FAILED");
    }

    // ── Helper Methods ────────────────────────────────────────────────────────

    private String resolveUser(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }

        // Fallback to request parameter email during registration or login
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof com.PrepTrack_AI.Fullstack_Project.dto.RegisterRequestDTO) {
                return ((com.PrepTrack_AI.Fullstack_Project.dto.RegisterRequestDTO) arg).getEmail();
            }
            if (arg instanceof com.PrepTrack_AI.Fullstack_Project.dto.LoginRequest) {
                return ((com.PrepTrack_AI.Fullstack_Project.dto.LoginRequest) arg).getEmail();
            }
        }
        return "SYSTEM";
    }

    private String getClientIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isEmpty()) {
                return xff.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        }
        return "0.0.0.0";
    }

    private Long extractId(Object result) {
        if (result == null) return null;
        try {
            if (result instanceof com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse) {
                Object data = ((com.PrepTrack_AI.Fullstack_Project.dto.ApiResponse<?>) result).getData();
                if (data == null) return null;
                // Try getId()
                try {
                    Method getId = data.getClass().getMethod("getId");
                    Object val = getId.invoke(data);
                    if (val instanceof Long) return (Long) val;
                    if (val instanceof Integer) return ((Integer) val).longValue();
                } catch (NoSuchMethodException e) {
                    // Try getUserId()
                    try {
                        Method getUserId = data.getClass().getMethod("getUserId");
                        Object val = getUserId.invoke(data);
                        if (val instanceof Long) return (Long) val;
                        if (val instanceof Integer) return ((Integer) val).longValue();
                    } catch (NoSuchMethodException ex) {
                        // Ignore
                    }
                }
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }
        return null;
    }

    private Long extractIdFromArgs(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
            if (arg instanceof Integer) {
                return ((Integer) arg).longValue();
            }
        }
        return null;
    }
}
