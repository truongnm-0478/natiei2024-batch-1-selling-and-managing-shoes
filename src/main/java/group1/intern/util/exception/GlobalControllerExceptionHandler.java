package group1.intern.util.exception;

import group1.intern.util.constant.ErrorMessageConstant;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.rmi.ServerError;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    // NoResourceFoundException
    @ExceptionHandler(value = NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException e, Model model) {
        model.addAttribute("message", "404 page not found!");
        return "error";
    }

    // ForbiddenException and UnauthorizedException
    @ExceptionHandler({ForbiddenException.class, UnauthorizedException.class, RuntimeException.class})
    public String handleForbiddenException(RuntimeException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    // AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException e, Model model) {
        model.addAttribute("message", ErrorMessageConstant.FORBIDDEN);
        return "error";
    }

    // AuthenticationCredentialsNotFoundException
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public String handleAuthenticationCredentialsNotFoundException(
        AuthenticationCredentialsNotFoundException ex, Model model) {
        model.addAttribute("message", ErrorMessageConstant.UNAUTHORIZED);
        return "error";
    }

    // MaxUploadSizeExceededException
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "File is too large!");
        return "redirect:/uploadStatus";
    }

    // Unsupported action
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        model.addAttribute("message", "Unsupported action: " + e.getMessage());
        return "error";
    }

    // Exception
    @ExceptionHandler({Exception.class, HttpServerErrorException.InternalServerError.class, ServerError.class})
    public String handleException(Exception e, Model model) {
        model.addAttribute("message", "500 internal server error with message: " + e.getMessage());
        return "error";
    }
}
